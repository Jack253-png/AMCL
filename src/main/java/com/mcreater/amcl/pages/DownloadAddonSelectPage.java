package com.mcreater.amcl.pages;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.items.BooleanListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.download.*;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class DownloadAddonSelectPage extends AbstractAnimationPage {
    static OriginalVersionModel model;
    static Label id;
    GridPane box;
    public BooleanListItem<Label> forge;
    public BooleanListItem<Label> optifine;
    public BooleanListItem<Label> fabric;
    public BooleanListItem<CurseFileLabel> optifabric;
    public BooleanListItem<CurseFileLabel> fabricapi;
    StringItem versionfinalName;
    public JFXButton install;
    public void closeAll(TitledPane pane){
        for (TitledPane t : J8Utils.createList(forge.cont.pane, optifine.cont.pane, fabric.cont.pane, optifabric.cont.pane, fabricapi.cont.pane)){
            if (t != pane) t.setExpanded(false);
        }
    }
    public void bindSingle(TitledPane pane){
        pane.setOnMouseReleased(event -> closeAll(pane));
    }
    public DownloadAddonSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.DOWNLOADMCPAGE;
        box = new GridPane();
        box.setAlignment(Pos.TOP_CENTER);
        FXUtils.ControlSize.setWidth(box, width);
        id = new Label();
        id.setFont(Fonts.s_f);
        forge = new BooleanListItem<>("Forge", width / 5 * 4);
        optifine = new BooleanListItem<>("OptiFine", width / 5 * 4);
        fabric = new BooleanListItem<>("Fabric", width / 5 * 4);
        optifabric = new BooleanListItem<>("OptiFabric", width / 5 * 4);
        fabricapi = new BooleanListItem<>("Fabric API", width / 5 * 4);
        forge.cont.setDisable(true);
        optifine.cont.setDisable(true);
        fabric.cont.setDisable(true);
        fabricapi.cont.setDisable(true);
        optifabric.cont.setDisable(true);
        bindSingle(forge.cont.pane);
        bindSingle(optifine.cont.pane);
        bindSingle(fabric.cont.pane);
        bindSingle(optifabric.cont.pane);
        bindSingle(fabricapi.cont.pane);
        forge.button.selectedProperty().addListener(event -> {
            forge.cont.setDisable(!forge.button.isSelected());
            if (forge.button.isSelected()){
                fabric.button.selectedProperty().set(false);
                optifabric.button.selectedProperty().set(false);
                fabricapi.button.selectedProperty().set(false);
            }
        });
        optifine.button.selectedProperty().addListener(event -> {
            optifine.cont.setDisable(!optifine.button.isSelected());
            if (fabric.button.isSelected()){
                optifabric.button.selectedProperty().set(optifine.button.isSelected());
            }
        });
        fabric.button.selectedProperty().addListener(event -> {
            fabric.cont.setDisable(!fabric.button.isSelected());
            if (fabric.button.isSelected()){
                forge.button.selectedProperty().set(false);
                if (optifine.button.isSelected()){
                    optifabric.button.selectedProperty().set(true);
                }
            }
            else{
                optifabric.button.selectedProperty().set(false);
                fabricapi.button.selectedProperty().set(false);
            }
        });
        optifabric.button.selectedProperty().addListener(event -> {
            optifabric.cont.setDisable(!optifabric.button.isSelected());
            if (optifabric.button.isSelected()){
                if (!fabric.button.isSelected() || !optifine.button.isSelected()){
                    optifabric.button.selectedProperty().set(false);
                }
            }
            else{
                if (fabric.button.isSelected()){
                    optifine.button.selectedProperty().set(false);
                }
            }
        });
        fabricapi.button.selectedProperty().addListener(event -> {
            fabricapi.cont.setDisable(!fabricapi.button.isSelected());
            if (fabricapi.button.isSelected()){
                if (!fabric.button.isSelected()){
                    fabricapi.button.selectedProperty().set(false);
                }
            }
        });
        install = new JFXButton();
        install.setFont(Fonts.s_f);
        install.setOnAction(event -> {
            install.setDisable(true);
            boolean forge = this.forge.button.isSelected();
            boolean optifine = this.optifine.button.isSelected();
            boolean fabric = this.fabric.button.isSelected();
            boolean fabricapi = this.fabricapi.button.isSelected();
            boolean optifabric = this.optifabric.button.isSelected();
            Label forgeItem = this.forge.cont.selectedItem;
            Label optifineItem = this.optifine.cont.selectedItem;
            Label fabricItem = this.fabric.cont.selectedItem;
            CurseFileLabel optifabricItem = this.optifabric.cont.selectedItem;
            CurseFileLabel fabricapiItem = this.fabricapi.cont.selectedItem;
            ProcessDialog dialog = new ProcessDialog(3, Launcher.languageManager.get("ui.install.title"));
            dialog.setV(0, 0);
            dialog.setV(1, 0);
            dialog.setV(2, 0);
            CountDownLatch latch = new CountDownLatch(1);
            String rl = versionfinalName.cont.getText();
            String versionDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions/%s", rl));
            if (new File(versionDir).exists()){
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.1"), "");
                install.setDisable(false);
                return;
            }
            else if (!isValidFileName(rl) || rl.equals("")){
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.2"), "");
                install.setDisable(false);
                return;
            }
            String modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "mods");
            if (Launcher.configReader.configModel.change_game_dir){
                modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions/%s/mods", rl));
            }

            if (!(forge || optifine || fabric || fabricapi || optifabric)){
                TaskManager.bind(dialog, 0);
                new Thread(() -> {
                    long millis = System.currentTimeMillis();
                    Platform.runLater(dialog::Create);
                    try {
                        OriginalDownload.download(Launcher.configReader.configModel.fastDownload,
                                this.model.id,
                                Launcher.configReader.configModel.selected_minecraft_dir_index,
                                rl,
                                Launcher.configReader.configModel.downloadChunkSize
                                );
                    } catch (Exception e) {
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                        throw new RuntimeException(e);
                    }
                    latch.countDown();
                    Platform.runLater(() -> install.setDisable(false));
                    dialog.setAll(100);
                    Platform.runLater(dialog::close);
                    long end = System.currentTimeMillis();
                    System.out.println((end - millis) / 1000);
                }).start();
            }
            else{
                if (forge){
                    TaskManager.bind(dialog, 0);
                    String finalModDir = modDir;
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            ForgeDownload.download(Launcher.configReader.configModel.fastDownload,
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    forgeItem.getText(),
                                    () -> TaskManager.bind(dialog, 1),
                                    () -> TaskManager.bind(dialog, 2)
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        latch.countDown();
                        if (optifine){
                            new Thread(() -> {
                                OptifineAPIModel model = GetVersionList.getOptifineVersionRaw();
                                String opti = null;
                                for (OptifineJarModel m : model.files){
                                    if (m.name.contains(this.model.id.replace("beta ", "beta_")) && m.name.contains(optifineItem.getText()))
                                    {
                                        opti = m.name;
                                        break;
                                    }
                                }
                                dialog.setV(0, 99, Launcher.languageManager.get("ui.install.optifine"));
                                new File(finalModDir).mkdirs();
                                try {
                                    new OptiFineInstallerDownloadTask(opti, LinkPath.link(finalModDir, opti)).execute();
                                } catch (Exception e) {
                                    dialog.setAll(100);
                                    Platform.runLater(dialog::close);
                                    throw new RuntimeException(e);
                                }
                                Platform.runLater(() -> install.setDisable(false));
                                dialog.setAll(100);
                                Platform.runLater(dialog::close);
                            }).run();
                        }
                        else {
                            Platform.runLater(() -> install.setDisable(false));
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                        }
                    }).start();
                }
                else if (fabric){
                    TaskManager.bind(dialog, 0);
                    String finalModDir1 = modDir;
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            FabricDownload.download(Launcher.configReader.configModel.fastDownload,
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    fabricItem.getText(),
                                    () -> TaskManager.bind(dialog, 1)
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        Vector<Task> tasks = new Vector<>();
                        new Thread(() -> {
                            if (optifine){
                                OptifineAPIModel model = GetVersionList.getOptifineVersionRaw();
                                String opti = null;
                                for (OptifineJarModel m : model.files) {
                                    if (m.name.contains(this.model.id.replace("beta ", "beta_")) && m.name.contains(optifineItem.getText())) {
                                        opti = m.name;
                                        break;
                                    }
                                }
                                dialog.setV(0, 99, Launcher.languageManager.get("ui.install.optifine"));
                                try {
                                    tasks.add(new OptiFineInstallerDownloadTask(opti, LinkPath.link(finalModDir1, opti)));
                                } catch (FileNotFoundException e) {
                                    dialog.setAll(100);
                                    Platform.runLater(dialog::close);
                                }
                            }
                            if (fabricapiItem != null) {
                                CurseModFileModel m = fabricapiItem.model;
                                try {
                                    tasks.add(new DownloadTask(m.downloadUrl, LinkPath.link(finalModDir1, m.fileName)));
                                } catch (FileNotFoundException e) {
                                    dialog.setAll(100);
                                    Platform.runLater(dialog::close);
                                }
                            }
                            if (optifabricItem != null) {
                                CurseModFileModel m1 = optifabricItem.model;
                                try {
                                    tasks.add(new DownloadTask(m1.downloadUrl, LinkPath.link(finalModDir1, m1.fileName)));
                                } catch (FileNotFoundException e) {
                                    dialog.setAll(100);
                                    Platform.runLater(dialog::close);
                                }
                            }
                        }).run();
                        TaskManager.addTasks(tasks);
                        TaskManager.bind(dialog, 2);
                        try {
                            TaskManager.execute("<fabric addons>");
                        } catch (Exception e) {

                        }
                        Platform.runLater(() -> install.setDisable(false));
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                    }).start();
                }
                else if (optifine){
                    TaskManager.bind(dialog, 0);
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            OptifineDownload.download(Launcher.configReader.configModel.fastDownload,
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    optifineItem.getText()
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        latch.countDown();
                        Platform.runLater(() -> install.setDisable(false));
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                    }).start();
                }
                install.setDisable(false);
            }
        });
        SettingPage p = new SettingPage(width, height - Launcher.barSize, box, false);
        versionfinalName = new StringItem("", width / 2);
        box.add(id, 0, 0, 1, 1);

        box.add(forge, 0, 1, 1, 1);
        box.add(optifine, 0, 2, 1, 1);
        box.add(fabric, 0, 3, 1, 1);
        box.add(fabricapi, 0, 4, 1, 1);
        box.add(optifabric, 0, 5, 1, 1);

        box.add(versionfinalName, 0, 6, 1, 1);
        box.add(install, 0, 7, 1, 1);
        ThemeManager.loadButtonAnimates(id, forge, optifine, fabric, fabricapi, optifabric, versionfinalName, install);
        this.add(p, 0, 0, 1, 1);
    }

    public static boolean isValidFileName(String fileName) {

        if (fileName == null || fileName.length() > 255) {
            return false;
        }
        else {
            Vector<String> invaildNames = new Vector<>(J8Utils.createList("con", "aux", "com1", "com2", "com3", "com4", "lpt1", "lpt2", "lpt3", "prn", "nul", ""));
            if (invaildNames.contains(fileName.toLowerCase())){
                return false;
            }
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }
    public void setVersionId(OriginalVersionModel model){
        DownloadAddonSelectPage.model = model;
        id.setText(model.id);
        LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.downloadaddonsselectpage.loading.title"));
        dialog.Create();
        new Thread(() -> {
            try {
                loadVers();
            }
            catch (Exception e){
                e.printStackTrace();
                Platform.runLater(() -> {
                    SimpleDialogCreater.create(Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"), Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"), Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.content"));
                    dialog.close();
                });
                Launcher.setPage(Launcher.DOWNLOADMCPAGE, this);
            }
            Platform.runLater(dialog::close);
        }).start();
    }
    public void loadVers() throws ParserConfigurationException, IOException, SAXException {
        Platform.runLater(() -> {
            forge.cont.clear();
            optifine.cont.clear();
            fabric.cont.clear();
            optifabric.cont.clear();
            fabricapi.cont.clear();
        });
        for (String forgev : GetVersionList.getForgeVersionList(model.id)){
            Label l = new Label(forgev);
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> forge.cont.addItem(l));
        }
        for (OptifineJarModel optiv : GetVersionList.getOptifineVersionList(model.id)){
            Label l = new Label(optiv.name);
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> optifine.cont.addItem(l));
        }
        for (String fabv : GetVersionList.getFabricVersionList(model.id)){
            Label l = new Label(fabv.replace("+build.", "."));
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> fabric.cont.addItem(l));
        }
        for (CurseModFileModel fabapav : GetVersionList.getFabricAPIVersionList(model.id)){
            CurseFileLabel l = new CurseFileLabel(fabapav.fileName.replace("fabric-api-", "").replace(".jar", ""));
            l.setFont(Fonts.t_f);
            l.model = fabapav;
            Platform.runLater(() -> fabricapi.cont.addItem(l));
        }
        for (CurseModFileModel optfabv : GetVersionList.getOptiFabricVersionList(model.id)){
            CurseFileLabel l = new CurseFileLabel(optfabv.fileName.replace("optifabric-", "").replace(".jar", ""));
            l.setFont(Fonts.t_f);
            l.model = optfabv;
            Platform.runLater(() -> optifabric.cont.addItem(l));
        }
        checkIsNull(forge);
        checkIsNull(optifine);
        checkIsNull(fabric);
        checkIsNull(optifabric);
        checkIsNull(fabricapi);
    }
    public static class CurseFileLabel extends Label{
        public CurseModFileModel model;

        public CurseFileLabel(String fileName) {
            super(fileName);
        }
    }
    public void checkIsNull(BooleanListItem<?> item){
        item.setDisable(item.cont.vecs.size() == 0);
        item.button.selectedProperty().set(false);
        item.button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                if (item.isDisable()){
                    item.button.selectedProperty().set(false);
                }
                else{
                    if (item.cont.vecs.size() > 0){
                        item.cont.select(0);
                    }
                }
            }
        });
    }

    public void refresh() {
        Platform.runLater(() -> versionfinalName.cont.setText(model.id));
    }

    public void refreshLanguage() {
        install.setText(Launcher.languageManager.get("ui.moddownloadpage.install.name"));
        versionfinalName.title.setText(Launcher.languageManager.get("ui.install.setname"));
    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
