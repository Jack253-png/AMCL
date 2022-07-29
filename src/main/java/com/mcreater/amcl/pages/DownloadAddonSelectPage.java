package com.mcreater.amcl.pages;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.items.BooleanListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.download.*;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.model.optifine.optifineAPIModel;
import com.mcreater.amcl.model.optifine.optifineJarModel;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.tasks.taskmanager.*;
import com.mcreater.amcl.tasks.AbstractTask;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.SetSize;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
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
    public DownloadAddonSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.DOWNLOADMCPAGE;
        box = new GridPane();
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.75)");
        box.setAlignment(Pos.TOP_CENTER);
        SetSize.set(box, width, height);
        id = new Label();
        id.setFont(Fonts.s_f);
        forge = new BooleanListItem<>("Forge", width);
        optifine = new BooleanListItem<>("OptiFine", width);
        fabric = new BooleanListItem<>("Fabric", width);
        optifabric = new BooleanListItem<>("OptiFabric", width);
        fabricapi = new BooleanListItem<>("Fabric API", width);
        SetSize.setWidth(forge.cont, width / 3);
        SetSize.setWidth(optifine.cont, width / 3);
        SetSize.setWidth(fabric.cont, width / 3);
        SetSize.setWidth(optifabric.cont, width / 3);
        SetSize.setWidth(fabricapi.cont, width / 3);
        SetSize.setHeight(forge, 40);
        SetSize.setHeight(optifine, 40);
        SetSize.setHeight(fabric, 40);
        SetSize.setHeight(optifabric, 40);
        SetSize.setHeight(fabricapi, 40);
        forge.cont.setDisable(true);
        optifine.cont.setDisable(true);
        fabric.cont.setDisable(true);
        fabricapi.cont.setDisable(true);
        optifabric.cont.setDisable(true);
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
            Label forgeItem = this.forge.cont.getSelectionModel().getSelectedItem();
            Label optifineItem = this.optifine.cont.getSelectionModel().getSelectedItem();
            Label fabricItem = this.fabric.cont.getSelectionModel().getSelectedItem();
            CurseFileLabel optifabricItem = this.optifabric.cont.getSelectionModel().getSelectedItem();
            CurseFileLabel fabricapiItem = this.fabricapi.cont.getSelectionModel().getSelectedItem();
            ProcessDialog dialog = new ProcessDialog(1, Launcher.languageManager.get("ui.install.title"));
            CountDownLatch latch = new CountDownLatch(1);
            String rl = versionfinalName.cont.getText();
            String versionDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s", rl));
            if (new File(versionDir).exists()){
                FastInfomation.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.1"), "");
                install.setDisable(false);
                return;
            }
            else if (!isValidFileName(rl)){
                FastInfomation.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.2"), "");
                install.setDisable(false);
                return;
            }
            String modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "mods");
            if (Launcher.configReader.configModel.change_game_dir){
                modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s\\mods", rl));
            }

            if (!(forge || optifine || fabric || fabricapi || optifabric)){
                TaskManager.bind(dialog, 0);
                new Thread(() -> {
                    Platform.runLater(dialog::Create);
                    try {
                        OriginalDownload.download(Launcher.configReader.configModel.fastDownload,
                                this.model.id,
                                Launcher.configReader.configModel.selected_minecraft_dir_index,
                                rl,
                                Launcher.configReader.configModel.downloadChunkSize
                                );
                    } catch (IOException | InterruptedException e) {
                        Platform.runLater(dialog::close);
                        throw new RuntimeException(e);
                    }
                    latch.countDown();
                    Platform.runLater(() -> install.setDisable(false));
                    Platform.runLater(dialog::close);
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
                                    forgeItem.getText()
                            );
                        } catch (IOException | InterruptedException | ParserConfigurationException | SAXException e) {
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        latch.countDown();
                        if (optifine){
                            new Thread(() -> {
                                String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
                                optifineAPIModel model = new Gson().fromJson(r, optifineAPIModel.class);
                                String opti = null;
                                for (optifineJarModel m : model.files){
                                    if (m.name.contains(this.model.id.replace("beta ", "beta_")) && m.name.contains(optifineItem.getText()))
                                    {
                                        opti = m.name;
                                        break;
                                    }
                                }
                                dialog.setV(0, 99, Launcher.languageManager.get("ui.install.optifine"));
                                try {
                                    new OptiFineInstallerDownloadTask(opti, LinkPath.link(finalModDir, opti)).execute();
                                } catch (IOException e) {
                                    Platform.runLater(dialog::close);
                                    throw new RuntimeException(e);
                                }
                                Platform.runLater(() -> install.setDisable(false));
                                Platform.runLater(dialog::close);
                            }).run();
                        }
                        else {
                            Platform.runLater(() -> install.setDisable(false));
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
                                    fabricItem.getText()
                            );
                        } catch (IOException | InterruptedException e) {
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        Vector<Task> tasks = new Vector<>();
                        new Thread(() -> {
                            if (optifine){
                                String r = HttpConnectionUtil.doGet("https://optifine.cn/api");
                                optifineAPIModel model = new Gson().fromJson(r, optifineAPIModel.class);
                                String opti = null;
                                for (optifineJarModel m : model.files) {
                                    if (m.name.contains(this.model.id.replace("beta ", "beta_")) && m.name.contains(optifineItem.getText())) {
                                        opti = m.name;
                                        break;
                                    }
                                }
                                dialog.setV(0, 99, Launcher.languageManager.get("ui.install.optifine"));
                                try {
                                    tasks.add(new OptiFineInstallerDownloadTask(opti, LinkPath.link(finalModDir1, opti)));
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (fabricapiItem != null) {
                                CurseModFileModel m = fabricapiItem.model;
                                try {
                                    tasks.add(new DownloadTask(m.downloadUrl, LinkPath.link(finalModDir1, m.fileName)));
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (optifabricItem != null) {
                                CurseModFileModel m1 = optifabricItem.model;
                                try {
                                    tasks.add(new DownloadTask(m1.downloadUrl, LinkPath.link(finalModDir1, m1.fileName)));
                                } catch (FileNotFoundException e) {
                                    Platform.runLater(dialog::close);
                                    throw new RuntimeException(e);
                                }
                            }
                        }).run();
                        TaskManager.addTasks(tasks);
                        try {
                            TaskManager.execute("<fabric addons>");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(() -> install.setDisable(false));
                        Platform.runLater(dialog::close);
                        for (Task t : tasks){
                            System.out.println(((AbstractTask) t).server);
                        }
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
                        } catch (IOException | InterruptedException | NoSuchMethodException | IllegalAccessException |
                                 InstantiationException | InvocationTargetException | ClassNotFoundException |
                                 NoSuchFieldException e) {
                            Platform.runLater(dialog::close);
                            throw new RuntimeException(e);
                        }
                        latch.countDown();
                        Platform.runLater(() -> install.setDisable(false));
                        Platform.runLater(dialog::close);
                    }).start();
                }
                install.setDisable(false);
            }
        });
        versionfinalName = new StringItem("", this.width / 2);
        versionfinalName.cont.setFont(Fonts.t_f);
        box.add(id, 0, 0, 1, 1);
        box.add(forge, 0, 1, 1, 1);
        box.add(optifine, 0, 2, 1, 1);
        box.add(fabric, 0, 3, 1, 1);
        box.add(fabricapi, 0, 4, 1, 1);
        box.add(optifabric, 0, 5, 1, 1);
        box.add(versionfinalName, 0, 6, 1, 1);
        box.add(install, 0, 7, 1, 1);
        this.add(box, 0, 0, 1, 1);
    }
    public void checkItem(boolean is, Node item) throws IOException {
        if (is && item == null){
            throw new IOException();
        }
    }
    public static boolean isValidFileName(String fileName) {

        if (fileName == null || fileName.length() > 255) {
            return false;
        }
        else {
            Vector<String> invaildNames = new Vector<>(List.of("con", "aux", "com1", "com2", "com3", "com4", "lpt1", "lpt2", "lpt3", "prn", "nul", ""));
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
            catch (ParserConfigurationException | IOException | SAXException e){
                e.printStackTrace();
                Platform.runLater(() -> {
                    FastInfomation.create(Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"), Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"), Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.content"));
                    dialog.close();
                });
                Launcher.setPage(Launcher.DOWNLOADMCPAGE, this);
            }
            Platform.runLater(dialog::close);
        }).start();
    }
    public void loadVers() throws ParserConfigurationException, IOException, SAXException {
        Platform.runLater(() -> {
            forge.cont.getItems().clear();
            optifine.cont.getItems().clear();
            fabric.cont.getItems().clear();
            optifabric.cont.getItems().clear();
            fabricapi.cont.getItems().clear();
        });
        for (String forgev : GetVersionList.getForgeVersionList(Launcher.configReader.configModel.fastDownload, model.id)){
            Label l = new Label(forgev);
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> forge.cont.getItems().add(l));
        }
        for (optifineJarModel optiv : GetVersionList.getOptifineVersionList(Launcher.configReader.configModel.fastDownload, model.id)){
            Label l = new Label(optiv.name);
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> optifine.cont.getItems().add(l));
        }
        for (String fabv : GetVersionList.getFabricVersionList(Launcher.configReader.configModel.fastDownload, model.id)){
            Label l = new Label(fabv);
            l.setFont(Fonts.t_f);
            Platform.runLater(() -> fabric.cont.getItems().add(l));
        }
        for (CurseModFileModel fabapav : GetVersionList.getFabricAPIVersionList(Launcher.configReader.configModel.fastDownload, model.id)){
            CurseFileLabel l = new CurseFileLabel(fabapav.fileName);
            l.setFont(Fonts.t_f);
            l.model = fabapav;
            Platform.runLater(() -> fabricapi.cont.getItems().add(l));
        }
        for (CurseModFileModel optfabv : GetVersionList.getOptiFabricVersionList(Launcher.configReader.configModel.fastDownload, model.id)){
            CurseFileLabel l = new CurseFileLabel(optfabv.fileName);
            l.setFont(Fonts.t_f);
            l.model = optfabv;
            Platform.runLater(() -> optifabric.cont.getItems().add(l));
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
        item.setDisable(item.cont.getItems().size() == 0);
        item.button.selectedProperty().set(false);
        item.button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                if (item.isDisable()){
                    item.button.selectedProperty().set(false);
                }
                else{
                    if (item.cont.getItems().size() > 0){
                        item.cont.getSelectionModel().select(0);
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
