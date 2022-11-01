package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.modApi.common.AbstractModFileModel;
import com.mcreater.amcl.api.modApi.common.AbstractModModel;
import com.mcreater.amcl.api.modApi.curseforge.CurseAPI;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.modApi.modrinth.ModrinthAPI;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;
import com.mcreater.amcl.api.modApi.modrinth.modFile.ModrinthModFileItemModel;
import com.mcreater.amcl.controls.ServerMod;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.dialogs.mod.ModrinthMultiFileDialog;
import com.mcreater.amcl.pages.dialogs.mod.RequiredModDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.FXUtils.Platform;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class ModDownloadPage extends AbstractAnimationPage {
    public Vector<CurseModModel> reqMods;
    VBox v;
    public Vector<ModFile> uis = new Vector<>();
    ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {};
    boolean coreSelected = false;
    ModFile last;
    Thread loadThread;
    GridPane p;
    public JFXButton install;
    AbstractModModel content;
    JFXButton getrc;
    SettingPage page;

    boolean loadSuccess = false;
    public static class DepencyModPage extends ModDownloadPage {
        public DepencyModPage(double width, double height, AbstractAnimationPage last) {
            super(width, height);
            l = last;
            ThemeManager.loadButtonAnimates(this);
        }
    }
    public ModDownloadPage(double width, double height) {
        super(width, height);
        reqMods = new Vector<>();
        l = Launcher.ADDMODSPAGE;
        p = new GridPane();
        v = new VBox();
        FXUtils.ControlSize.set(p, width, height);
        page = new SettingPage(800, 480 - 45 - 70, v, false);
        p.add(page, 0, 0, 1, 1);

        install = new JFXButton();
        install.setButtonType(JFXButton.ButtonType.RAISED);
        install.setFont(Fonts.t_f);
        install.setOnAction(event -> {
            if (coreSelected) {
                ProcessDialog dialog = new ProcessDialog(1, Launcher.languageManager.get("ui.moddownloadpage.downloadingMods.title"));
                dialog.setV(0, 5, Launcher.languageManager.get("ui.downloadmod._01"));
                Thread t = new Thread(() -> {
                    try {
                        dialog.Create();
                        dialog.setV(0, 7, Launcher.languageManager.get("ui.downloadmod._02"));
                        dialog.setV(0, 10, Launcher.languageManager.get("ui.downloadmod._03"));
                        Vector<DownloadTask> tasks = new Vector<>();
                        String modPath;
                        if (Launcher.configReader.configModel.change_game_dir){
                            modPath = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "versions/" + Launcher.configReader.configModel.selected_version_index + "/mods");
                        }
                        else {
                            modPath = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "mods");
                        }
                        if (last.model.isCurseFile()) {
                            if (last.model.toCurseFile().downloadUrl != null) {
                                if (!last.model.toCurseFile().fileName.endsWith(".zip")) {
                                    tasks.add(new DownloadTask(last.model.toCurseFile().downloadUrl, LinkPath.link(modPath, last.model.toCurseFile().fileName), Launcher.configReader.configModel.downloadChunkSize));
                                }
                            }
                        }
                        else {
                            ModrinthModFileItemModel model = null;
                            Vector<ModrinthModFileItemModel> files = new Vector<>(last.model.toModrinthFile().files);

                            files.removeIf(modrinthModFileItemModel -> !modrinthModFileItemModel.filename.endsWith(".jar"));

//                            if (last.model.toModrinthFile().files.size() >= 1) {
//                                for (ModrinthModFileItemModel m : last.model.toModrinthFile().files) {
//                                    if (m.filename.endsWith(".jar")) {
//                                        model = m;
//                                        break;
//                                    }
//                                }
//                            }

                            Vector<String> f = new Vector<>();
                            files.forEach(modrinthModFileItemModel -> f.add(modrinthModFileItemModel.filename));

                            AtomicInteger index = new AtomicInteger();
                            AtomicBoolean selected = new AtomicBoolean(false);

                            if (files.size() > 1) {
                                Platform.runLater(() -> {
                                    index.set(new ModrinthMultiFileDialog(f, Launcher.languageManager.get("ui.moddownloadpage.modrinth.multifile.title")).getIndex());
                                    selected.set(true);
                                });
                            }
                            else {
                                selected.set(true);
                            }

                            do {} while (!selected.get());

                            if (files.size() >= 1) {
                                model = files.get(index.get());
                                tasks.add(new DownloadTask(model.url, LinkPath.link(modPath, model.filename), Launcher.configReader.configModel.downloadChunkSize));
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
                    catch (Exception e) {
                        SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.title"));
                        e.printStackTrace();
                    } finally {
                        Platform.runLater(dialog::close);
                    }
                });
                t.start();
            }
            else{
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.title"), Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.content"), "");
            }
        });
        getrc = new JFXButton(Launcher.languageManager.get("ui.moddownloadpage.getrequire.name"));
        getrc.setOnAction(event -> {
            LoadingDialog dialog2 = new LoadingDialog(Launcher.languageManager.get("ui.moddownloadpage.getrequire.process.name"));
            dialog2.Create();
            CountDownLatch latch = new CountDownLatch(1);
            RequiredModDialog dialog = new RequiredModDialog(Launcher.languageManager.get("ui.moddownloadpage.getrequire.dialog.title"));
            new Thread(() -> {
                try {
                    if (last != null) {
                        if (last.model.isCurseFile()) {
                            Vector<? extends AbstractModModel> models = last.model.isCurseFile() ? CurseAPI.getModFileRequiredMods(last.model.toCurseFile()) : ModrinthAPI.getModFileRequiredMods(last.model.toModrinthFile());

                            for (AbstractModModel model : models) {
                                Platform.runLater(() -> dialog.items.addItem(new ServerMod(model)));
                            }

                            DepencyModPage page = new DepencyModPage(width, height, MODDOWNLOADPAGE);

                            dialog.items.setOnAction(() -> {
                                dialog.close();
                                page.setModContent(dialog.items.selectedItem.model);
                                Launcher.setPage(page, this);
                            });
                        }
                        else {
                            if (last.model.isCurseFile()) {
                                for (CurseModModel model : CurseAPI.getModFileRequiredMods(last.model.toCurseFile())) {
                                    Platform.runLater(() -> dialog.items.addItem(new ServerMod(model)));
                                }
                            }
                            else {
                                for (ModrinthModModel model : ModrinthAPI.getModFileRequiredMods(last.model.toModrinthFile())) {
                                    Platform.runLater(() -> dialog.items.addItem(new ServerMod(model)));
                                }
                            }
                            DepencyModPage page = new DepencyModPage(width, height, MODDOWNLOADPAGE);

                            dialog.items.setOnAction(() -> {
                                dialog.close();
                                page.setModContent(dialog.items.selectedItem.model);
                                Launcher.setPage(page, this);
                            });
                        }
                        dialog.Create();
                    }
                    else {
                        SimpleDialogCreater.create(Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.title"), Launcher.languageManager.get("ui.moddownloadpage.coreNotSelected.content"), "");
                    }
                }
                catch (Exception e){
                    SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mod.load"));
                }
                finally {
                    latch.countDown();
                    Platform.runLater(dialog2::close);
                }
            }).start();
        });

        HBox box = new HBox(install, getrc);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox t = new HBox(new Label("    "), box);

        ThemeManager.applyNode(t);
        t.setId("modinstall");
        FXUtils.ControlSize.set(t, this.width, 70);
        p.add(t, 0, 1, 1, 1);
        this.add(p, 0, 0, 1, 1);
        nodes.add(null);
        BindedPageproperty().get().addAll(J8Utils.createList(
                ADDMODSPAGE,
                CONFIGPAGE,
                DOWNLOADADDONSELECTPAGE,
                DOWNLOADMCPAGE,
                MODDOWNLOADPAGE,
                USERSELECTPAGE,
                VERSIONINFOPAGE,
                VERSIONSELECTPAGE
        ));
    }
    public void setModContent(AbstractModModel model){
        if (this.content != model || !loadSuccess) {
            this.uis.clear();
            this.v.getChildren().clear();
            this.content = model;
            loadSuccess = false;
            v.setDisable(true);
            install.setDisable(true);
            getrc.setDisable(true);
            do {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            } while (this.v.getChildren().size() != 0);

            loadThread = new Thread(() -> {
                try {
                    Map<String, ? extends Vector<? extends AbstractModFileModel>> files;
                    if (model.isCurseMod()) {
                        files = CurseAPI.getModFiles(model.toCurseMod());
                    }
                    else {
                        files = ModrinthAPI.getModFiles(model.toModrinthMod());
                    }

                    Vector<String> vers = new Vector<>(files.keySet());
                    vers.sort(new VersionComparsion(GetVersionList.getOriginalList(FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer))));

                    for (String s1 : vers) {
                        TitledPane pane = new TitledPane();
                        pane.setText(s1);
                        pane.setExpanded(false);
                        pane.setFont(Fonts.t_f);
                        FXUtils.ControlSize.setWidth(pane, 800);
                        VBox b = new VBox();
                        for (AbstractModFileModel u : files.get(s1)) {
                            b.getChildren().add(new ModFile(u, s1));
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

                        ThemeManager.applyNode(pane);
                        FXUtils.ControlSize.setWidth(pane, this.width - 15);
                        FXUtils.ControlSize.setWidth(b, this.width - 15);
                        FXUtils.ControlSize.setWidth(v, this.width - 15);
                        Platform.runLater(() -> v.getChildren().add(pane));
                        Platform.runLater(() -> ThemeManager.loadButtonAnimates(pane));
                        Sleeper.sleep(25);
                    }
                    Platform.runLater(() -> {
                        v.setDisable(false);
                        install.setDisable(false);
                        getrc.setDisable(false);
                    });
                    loadSuccess = true;
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.moddownloadpage.loadversions.fail.title"));
                        Launcher.setPage(l, this);
                    });
                    e.printStackTrace();
                }
            });
            loadThread.start();
        }
    }
    public void refresh() {

    }
    public void refreshLanguage() {
        this.name = Launcher.languageManager.get("ui.moddownloadpage.name");
        install.setText(Launcher.languageManager.get("ui.moddownloadpage.install.name"));
    }
    public void refreshType() {

    }
    public void onExitPage() {
        if (!loadSuccess) {
            if (loadThread != null) {
                loadThread.stop();
            }
            this.uis.clear();
            this.v.getChildren().clear();
        }
    }
    public static Date getTimeTick(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat1.parse(J8Utils.createList(time.split("\\.")).get(0).replace("T", " "));
    }
    public static class VersionComparsion implements Comparator<String> {
        Vector<OriginalVersionModel> vers;
        public VersionComparsion(Vector<OriginalVersionModel> vers) {
            this.vers = vers;
        }
        private int getIndex(String ver) {
            int l = -1;
            for (int g = 0; g < vers.size(); g++) {
                if (vers.get(g).id.equals(ver.replace("1.14-pre", "1.14 Pre-Release ").replace("1.14.1-pre", "1.14.1 Pre-Release ").replace("1.14.2-pre", "1.14.2 Pre-Release "))) {
                    l = g;
                    break;
                }
            }
            return l;
        }
        public int compare(String compareValue1, String compareValue2) {
            int id1 = getIndex(clearSnapShotVersion(compareValue1));
            int id2 = getIndex(clearSnapShotVersion(compareValue2));

            if (id1 == id2) return 0;
            return id1 > id2 ? -1 : 1;
        }
        public String clearSnapShotVersion(String raw){
            return raw.replace("-Snapshot", "-pre1");
        }
    }
}
