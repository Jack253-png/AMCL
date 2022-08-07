package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.FXUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModDownloadPage extends AbstractAnimationPage {
    public Vector<CurseModModel> reqMods;
    VBox v;
    public Vector<ModFile> uis = new Vector<>();
    ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {};
    boolean coreSelected = false;
    ModFile last;
    Thread loadThread;
    GridPane p;
    public JFXCheckBox installRequires;
    public JFXButton install;
    CurseModModel content;
    public ModDownloadPage(double width, double height) {
        super(width, height);
        reqMods = new Vector<>();
        l = Launcher.ADDMODSPAGE;
        p = new GridPane();
        v = new VBox();
        FXUtils.ControlSize.set(p, width, height);
        p.add(new SettingPage(800, 480 - 45 - 70, v, false), 0, 0, 1, 1);
        installRequires = new JFXCheckBox();
        installRequires.selectedProperty().set(true);
        installRequires.setFont(Fonts.t_f);
        install = new JFXButton();
        install.setButtonType(JFXButton.ButtonType.RAISED);
        install.setFont(Fonts.t_f);
        install.setOnAction(event -> {
            if (coreSelected) {
                AtomicReference<Vector<CurseModFileModel>> requireMods = new AtomicReference<>();
                ProcessDialog dialog = new ProcessDialog(1, Launcher.languageManager.get("ui.moddownloadpage.downloadingMods.title"));
                dialog.setV(0, 5, Launcher.languageManager.get("ui.downloadmod._01"));
                Thread t = new Thread(() -> {
                    try {
                        dialog.Create();
                        dialog.setV(0, 7, Launcher.languageManager.get("ui.downloadmod._02"));
                        if (installRequires.isSelected()) {
                            requireMods.set(CurseAPI.getModFileRequiredMods(last.model, last.version, last.model.fileName));
                        }
                        requireMods.get().add(last.model);
                        dialog.setV(0, 10, Launcher.languageManager.get("ui.downloadmod._03"));
                        Vector<DownloadTask> tasks = new Vector<>();
                        for (CurseModFileModel model : requireMods.get()){
                            String modPath;
                            if (Launcher.configReader.configModel.change_game_dir){
                                modPath = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "versions\\" + Launcher.configReader.configModel.selected_version_index + "\\mods");
                            }
                            else {
                                modPath = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "mods");
                            }
                            if (model.downloadUrl != null) {
                                tasks.add(new DownloadTask(model.downloadUrl, LinkPath.link(modPath, model.fileName), 2048));
                            }
                        }
                        AtomicInteger downloaded = new AtomicInteger();
                        for (DownloadTask task : tasks){
                            new Thread(() -> {
                                while (true) {
                                    try {
                                        task.execute();
                                        downloaded.addAndGet(1);
                                        break;
                                    } catch (IOException ignored) {}
                                }
                            }).start();
                        }
                        do {
                            Thread.sleep(500);
                            double processTemp = (double) downloaded.get() / (double)  tasks.size();
                            dialog.setV(0, (int) (10 + 90 * processTemp), String.format(Launcher.languageManager.get("ui.downloadmod._04"), downloaded.get(), tasks.size()));
                        } while (downloaded.get() != tasks.size());
                    }
                    catch (IOException e) {
                        Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.title"), String.format(Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.content"), e), ""));
                    } catch (InterruptedException ignored) {
                        ignored.printStackTrace();
                    }
                    finally {
                        Platform.runLater(dialog::close);
                    }
                });
                t.start();
            }
            else{
                FastInfomation.create(Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.title"), Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.content"), "");
            }
        });
        VBox box = new VBox(installRequires, install);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox t = new HBox(new Label("    "), box);
        t.setId("modinstall");
        FXUtils.ControlSize.set(t, this.width, 70);
        p.add(t, 0, 1, 1, 1);
        this.add(p, 0, 0, 1, 1);
    }
    public void setModContent(CurseModModel model){
        this.content = model;
        this.setDisable(true);
        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        } while (this.v.getChildren().size() != 0);
        loadThread = new Thread(() -> {
            try {
                Vector<String> versions = new Vector<>();
                Vector<CurseModFileModel> files = CurseAPI.getModFiles(model);
                for (CurseModFileModel m : files) {
                    for (String s : ModFile.getModLoaders(m.gameVersions, false)) {
                        if (!versions.contains(s)) {
                            versions.add(s);
                        }
                    }
                }
                versions.sort(new VersionComparsion());
                for (String s1 : versions) {
                    TitledPane pane = new TitledPane();
                    pane.setText(s1);
                    pane.setExpanded(false);
                    pane.setFont(Fonts.t_f);
                    FXUtils.ControlSize.setWidth(pane, 800);
                    VBox b = new VBox();
                    for (CurseModFileModel u : files) {
                        getTimeTick(u.fileDate);
                        if (u.gameVersions.contains(s1)) {
                            b.getChildren().add(new ModFile(u, s1));
                        }
                    }
                    List<Node> list = new Vector<>(b.getChildren());
                    list.sort(Comparator.comparing(node -> ((ModFile) node)));
                    b.getChildren().clear();
                    b.getChildren().addAll(list);
                    pane.setContent(b);
                    for (Node n : b.getChildren()) {
                        ModFile file = (ModFile) n;
                        uis.add(file);
                        changeListener = (observable, oldValue, newValue) -> {
                            if (last == file || last == null) {
                                coreSelected = newValue;
                            }
                            if (newValue) {
                                last = file;
                                int temp = uis.indexOf(file);
                                for (int i = 0; i < uis.size(); i++) {
                                    if (i != temp) {
                                        uis.get(i).checkBox.selectedProperty().set(false);
                                    }
                                }
                            }
                        };
                        file.checkBox.selectedProperty().addListener(this.changeListener);
                    }
                    FXUtils.ControlSize.setWidth(pane, this.width - 15);
                    FXUtils.ControlSize.setWidth(b, this.width - 15);
                    FXUtils.ControlSize.setWidth(v, this.width - 15);
                    Platform.runLater(() -> v.getChildren().add(pane));
                }
                ThemeManager.loadButtonAnimates(v.getChildren().toArray(new Node[0]));
                this.setDisable(false);
            }
            catch (IOException e){
                Platform.runLater(() -> {
                    FastInfomation.create(Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.title"), String.format(Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.content"), e), "");
                    Launcher.setPage(Launcher.ADDMODSPAGE, this);
                });
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        );
        loadThread.start();
    }
    public void refresh() {

    }
    public void refreshLanguage() {
        this.name = Launcher.languageManager.get("ui.moddownloadpage.name");
        installRequires.setText(Launcher.languageManager.get("ui.moddownloadpage.installReq.name"));
        install.setText(Launcher.languageManager.get("ui.moddownloadpage.install.name"));
    }
    public void refreshType() {

    }
    public void onExitPage() {
        if (loadThread != null){
            loadThread.stop();
        }
        this.uis.clear();
        this.v.getChildren().clear();
    }
    public static Date getTimeTick(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat1.parse(List.of(time.split("\\.")).get(0).replace("T", " "));
        return date;
    }
    public static class VersionComparsion implements Comparator<String> {
        public int compare(String compareValue1, String compareValue2) {
            compareValue1 = clearSnapShotVersion(compareValue1);
            compareValue2 = clearSnapShotVersion(compareValue2);
            String[] valueSplit1 = compareValue1.split("[.]");
            String[] valueSplit2 = compareValue2.split("[.]");
            int minLength = valueSplit1.length;
            if (minLength > valueSplit2.length) {
                minLength = valueSplit2.length;
            }
            for (int i = 0; i < minLength; i++) {
                int value1 = Integer.parseInt(valueSplit1[i]);
                int value2 = Integer.parseInt(valueSplit2[i]);
                if(value1 > value2){
                    return 1;
                }else if(value1 < value2){
                    return -1;
                }
            }
            return valueSplit1.length - valueSplit2.length;
        }
        public String clearSnapShotVersion(String raw){
            return raw.replace("-Snapshot", "");
        }
    }
}
