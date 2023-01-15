package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.modApi.curseforge.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.items.BooleanListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.download.FabricDownload;
import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.download.ForgeOptifineDownload;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.download.OptifineDownload;
import com.mcreater.amcl.download.OriginalDownload;
import com.mcreater.amcl.download.QuiltDownload;
import com.mcreater.amcl.model.download.NewForgeItemModel;
import com.mcreater.amcl.model.download.OriginalVersionModel;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.model.optifine.OptifineAPIModel;
import com.mcreater.amcl.model.optifine.OptifineJarModel;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.LambdaTask;
import com.mcreater.amcl.tasks.OptiFineInstallerDownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class DownloadAddonSelectPage extends AbstractAnimationPage {
    static OriginalVersionModel model;
    static Label id;
    GridPane box;
    public BooleanListItem<ForgeLabel> forge;
    public BooleanListItem<Label> optifine;
    public BooleanListItem<Label> fabric;
    public BooleanListItem<CurseFileLabel> optifabric;
    public BooleanListItem<CurseFileLabel> fabricapi;
    public BooleanListItem<Label> quilt;
    public BooleanListItem<CurseFileLabel> quiltapi;
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
        forge = new BooleanListItem<>("Forge", width / 5 * 4, VersionTypeGetter.VersionType.FORGE);
        optifine = new BooleanListItem<>("OptiFine", width / 5 * 4, VersionTypeGetter.VersionType.OPTIFINE);
        fabric = new BooleanListItem<>("Fabric", width / 5 * 4, VersionTypeGetter.VersionType.FABRIC);
        optifabric = new BooleanListItem<>("OptiFabric", width / 5 * 4,  VersionTypeGetter.VersionType.FABRIC);
        fabricapi = new BooleanListItem<>("Fabric API", width / 5 * 4, VersionTypeGetter.VersionType.FABRIC);
        quilt = new BooleanListItem<>("Quilt", width / 5 * 4, VersionTypeGetter.VersionType.QUILT);
        quiltapi = new BooleanListItem<>("Quilt API", width / 5 * 4, VersionTypeGetter.VersionType.QUILT);

        forge.cont.setDisable(true);
        optifine.cont.setDisable(true);
        fabric.cont.setDisable(true);
        fabricapi.cont.setDisable(true);
        optifabric.cont.setDisable(true);
        quilt.cont.setDisable(true);
        quiltapi.cont.setDisable(true);
        bindSingle(forge.cont.pane);
        bindSingle(optifine.cont.pane);
        bindSingle(fabric.cont.pane);
        bindSingle(optifabric.cont.pane);
        bindSingle(fabricapi.cont.pane);
        bindSingle(quilt.cont.pane);
        bindSingle(quiltapi.cont.pane);

        forge.button.selectedProperty().addListener(event -> {
            forge.cont.setDisable(!forge.button.isSelected());
            if (forge.button.isSelected()){
                fabric.button.selectedProperty().set(false);
                optifabric.button.selectedProperty().set(false);
                fabricapi.button.selectedProperty().set(false);
                quilt.button.selectedProperty().set(false);
                quiltapi.button.selectedProperty().set(false);
            }
        });
        optifine.button.selectedProperty().addListener(event -> {
            optifine.cont.setDisable(!optifine.button.isSelected());
            if (optifine.button.isSelected()) {
                quilt.button.selectedProperty().set(false);
                quiltapi.button.selectedProperty().set(false);
            }
            if (fabric.button.isSelected()){
                optifabric.button.selectedProperty().set(optifine.button.isSelected());
            }
        });
        fabric.button.selectedProperty().addListener(event -> {
            fabric.cont.setDisable(!fabric.button.isSelected());
            if (fabric.button.isSelected()){
                forge.button.selectedProperty().set(false);
                quilt.button.selectedProperty().set(false);
                quiltapi.button.selectedProperty().set(false);
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
            else {
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
        quilt.button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            quilt.cont.setDisable(!quilt.button.isSelected());
            if (quilt.button.isSelected()) {
                optifine.button.selectedProperty().set(false);
                forge.button.selectedProperty().set(false);
                fabric.button.selectedProperty().set(false);
                optifabric.button.selectedProperty().set(false);
                fabricapi.button.selectedProperty().set(false);
            }
            else {
                if (quiltapi.button.isSelected()) {
                    quiltapi.button.selectedProperty().set(false);
                }
            }
        });
        quiltapi.button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            quiltapi.cont.setDisable(!quiltapi.button.isSelected());
            if (quiltapi.button.isSelected()) {
                if (!quilt.button.isSelected()) {
                    quiltapi.button.selectedProperty().set(false);
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
            boolean quilt = this.quilt.button.isSelected();
            boolean quiltapi = this.quiltapi.button.isSelected();
            ForgeLabel forgeItem = this.forge.cont.selectedItem;
            Label optifineItem = this.optifine.cont.selectedItem;
            Label fabricItem = this.fabric.cont.selectedItem;
            CurseFileLabel optifabricItem = this.optifabric.cont.selectedItem;
            CurseFileLabel fabricapiItem = this.fabricapi.cont.selectedItem;
            Label quiltItem = this.quilt.cont.selectedItem;
            CurseFileLabel quiltapiItem = this.quiltapi.cont.selectedItem;

            ProcessDialog dialog = new ProcessDialog(3, Launcher.languageManager.get("ui.install.title"));
            dialog.setV(0, 0);
            dialog.setV(1, 0);
            dialog.setV(2, 0);
            CountDownLatch latch = new CountDownLatch(1);
            String rl = versionfinalName.cont.getText();
            String versionDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s"), rl));
            if (new File(versionDir).exists()){
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.1"), "");
                install.setDisable(false);
                return;
            }
            else if (!isValidFileName(rl) || rl.equals("") || rl.endsWith(" ")){
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.2"), "");
                install.setDisable(false);
                return;
            }
            String modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "mods");
            if (Launcher.configReader.configModel.change_game_dir){
                modDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s", "mods"), rl));
            }

            if (!(forge || optifine || fabric || fabricapi || optifabric || quilt)){
                TaskManager.setUpdater((value, mess) -> dialog.setV(0, value, mess));
                new Thread(() -> {
                    Platform.runLater(dialog::Create);
                    try {
                        OriginalDownload.download(
                                this.model.id,
                                Launcher.configReader.configModel.selected_minecraft_dir_index,
                                rl,
                                Launcher.configReader.configModel.downloadChunkSize,
                                Launcher.configReader.configModel.downloadServer
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
            else{
                if (forge){
                    TaskManager.setUpdater((value, mess) -> dialog.setV(0, value, mess));
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        if (!optifine) {
                            try {
                                ForgeDownload.download(
                                        this.model.id,
                                        Launcher.configReader.configModel.selected_minecraft_dir_index,
                                        rl,
                                        Launcher.configReader.configModel.downloadChunkSize,
                                        forgeItem.model,
                                        () -> dialog.setV(Launcher.languageManager.get("ui.download.forge.installer")),
                                        () -> TaskManager.setUpdater((value, mess) -> dialog.setV(1, value, mess)),
                                        () -> TaskManager.setUpdater((value, mess) -> dialog.setV(2, value, mess)),
                                        Launcher.configReader.configModel.downloadServer
                                );
                            } catch (Exception e) {
                                dialog.setAll(100);
                                Platform.runLater(dialog::close);
                                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mcdownload"));
                                return;
                            }
                            latch.countDown();
                        }
                        else {
                            try {
                                ForgeOptifineDownload.download(
                                        this.model.id,
                                        Launcher.configReader.configModel.selected_minecraft_dir_index,
                                        rl,
                                        Launcher.configReader.configModel.downloadChunkSize,
                                        forgeItem.model,
                                        () -> dialog.setV(Launcher.languageManager.get("ui.download.forge.installer")),
                                        () -> TaskManager.setUpdater((value, mess) -> dialog.setV(1, value, mess)),
                                        () -> TaskManager.setUpdater((value, mess) -> dialog.setV(2, value, mess)),
                                        optifineItem.getText(),
                                        () -> dialog.setV(Launcher.languageManager.get("ui.download.optifine.installer")),
                                        () -> dialog.setV(Launcher.languageManager.get("ui.download.optifine.injecting")),
                                        Launcher.configReader.configModel.downloadServer
                                );
                            } catch (Exception e) {
                                dialog.setAll(100);
                                Platform.runLater(dialog::close);
                                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mcdownload"));
                                return;
                            }
                        }
                        Platform.runLater(() -> install.setDisable(false));
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                    }).start();
                }
                else if (fabric){
                    TaskManager.setUpdater((value, mess) -> dialog.setV(0, value, mess));
                    String finalModDir1 = modDir;
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            FabricDownload.download(
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    fabricItem.getText(),
                                    () -> TaskManager.setUpdater((value, mess) -> dialog.setV(1, value, mess)),
                                    Launcher.configReader.configModel.downloadServer
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mcdownload"));
                            return;
                        }
                        TaskManager.setUpdater((value, mess) -> dialog.setV(2, value, mess));

                        dialog.setV(0, 100);
                        Vector<Task> tasks = new Vector<>();
                        if (optifine){
                            OptifineAPIModel model;
                            try {
                                model = GetVersionList.getOptifineVersionRaw();
                            }
                            catch (Exception e){
                                dialog.close();
                                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"));
                                return;
                            }
                            String opti = null;
                            for (OptifineJarModel m : model.files) {
                                if (m.name.contains(this.model.id.replace("beta ", "beta_")) && m.name.contains(optifineItem.getText())) {
                                    opti = m.name;
                                    break;
                                }
                            }
                            dialog.setV(0, 99, Launcher.languageManager.get("ui.install.optifine"));
                            tasks.add(new OptiFineInstallerDownloadTask(opti, LinkPath.link(finalModDir1, opti)));
                        }
                        if (fabricapi && fabricapiItem != null) {
                            CurseModFileModel m = fabricapiItem.model;
                            tasks.add(new DownloadTask(m.downloadUrl, LinkPath.link(finalModDir1, m.fileName)));
                        }
                        if (optifabric && optifabricItem != null) {
                            CurseModFileModel m1 = optifabricItem.model;
                            tasks.add(new DownloadTask(m1.downloadUrl, LinkPath.link(finalModDir1, m1.fileName)));
                        }
                        TaskManager.addTasks(tasks);
                        try {
                            TaskManager.execute("<fabric addons>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> install.setDisable(false));
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                    }).start();
                }
                else if (quilt) {
                    TaskManager.setUpdater((value, mess) -> dialog.setV(0, value, mess));
                    String finalModDir2 = modDir;
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            QuiltDownload.download(
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    quiltItem.getText(),
                                    () -> TaskManager.setUpdater((value, mess) -> dialog.setV(1, value, mess)),
                                    Launcher.configReader.configModel.downloadServer
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mcdownload"));
                            return;
                        }
                        TaskManager.setUpdater((value, mess) -> dialog.setV(2, value, mess));

                        Vector<Task> tasks = new Vector<>();
                        if (quiltapi && quiltapiItem != null) {
                            CurseModFileModel m1 = quiltapiItem.model;
                            tasks.add(new DownloadTask(m1.downloadUrl, LinkPath.link(finalModDir2, m1.fileName)));
                        }
                        TaskManager.addTasks(tasks);
                        try {
                            TaskManager.execute("<quilt addons>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(() -> install.setDisable(false));
                        dialog.setAll(100);
                        Platform.runLater(dialog::close);
                    }).start();
                }
                else if (optifine){
                    TaskManager.setUpdater((value, mess) -> dialog.setV(0, value, mess));
                    new Thread(() -> {
                        Platform.runLater(dialog::Create);
                        try {
                            OptifineDownload.download(
                                    this.model.id,
                                    Launcher.configReader.configModel.selected_minecraft_dir_index,
                                    rl,
                                    Launcher.configReader.configModel.downloadChunkSize,
                                    optifineItem.getText(),
                                    () -> dialog.setV(Launcher.languageManager.get("ui.download.optifine.installer")),
                                    () -> dialog.setV(Launcher.languageManager.get("ui.download.optifine.injecting")),
                                    Launcher.configReader.configModel.downloadServer
                            );
                        } catch (Exception e) {
                            dialog.setAll(100);
                            Platform.runLater(dialog::close);
                            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.exceptions.mcdownload"));
                            return;
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
        AdvancedScrollPane p = new AdvancedScrollPane(width, height - Launcher.barSize, box, false);
        versionfinalName = new StringItem("", width / 2);
        box.add(id, 0, 0, 1, 1);

        box.add(forge, 0, 1, 1, 1);
        box.add(optifine, 0, 2, 1, 1);
        box.add(fabric, 0, 3, 1, 1);
        box.add(fabricapi, 0, 4, 1, 1);
        box.add(optifabric, 0, 5, 1, 1);
        box.add(quilt, 0, 6, 1, 1);
        box.add(quiltapi, 0, 7, 1, 1);

        box.add(versionfinalName, 0, 8, 1, 1);
        box.add(install, 0, 9, 1, 1);
        ThemeManager.loadNodeAnimations(id, forge, optifine, fabric, fabricapi, optifabric, versionfinalName, install, quilt, quiltapi);
        this.add(p, 0, 0, 1, 1);

        nodes.add(null);
        bindedPageproperty().get().addAll(J8Utils.createList(
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

    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255) {
            return false;
        }
        else {
            if (OSInfo.isWin()) {
                Vector<String> invaildNames = new Vector<>(J8Utils.createList("con", "aux", "com1", "com2", "com3", "com4", "lpt1", "lpt2", "lpt3", "prn", "nul", ""));
                if (invaildNames.contains(fileName.toLowerCase())) {
                    return false;
                }
                return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
            }
            else {
                return fileName.contains("\\") ||
                        fileName.contains("/");
            }
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
                Platform.runLater(dialog::close);
                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"));
                Launcher.setPage(Launcher.DOWNLOADMCPAGE, this);
            }
            Platform.runLater(dialog::close);
        }).start();
    }
    public void loadVers() throws Exception {
        Platform.runLater(() -> {
            forge.cont.clear();
            optifine.cont.clear();
            fabric.cont.clear();
            optifabric.cont.clear();
            fabricapi.cont.clear();

            forge.cont.pane.setExpanded(false);
            optifine.cont.pane.setExpanded(false);
            fabric.cont.pane.setExpanded(false);
            optifabric.cont.pane.setExpanded(false);
            fabricapi.cont.pane.setExpanded(false);
        });
        Vector<Task> t = new Vector<>();

        t.addAll(J8Utils.createList(
            new LambdaTask(() -> {
                try {
                    if (GetVersionList.isMirror(Launcher.configReader.configModel.downloadServer)) {
                        for (NewForgeItemModel model1 : GetVersionList.getForgeInstallers(model.id, Launcher.configReader.configModel.downloadServer)) {
                            ForgeLabel l = new ForgeLabel(model1);
                            l.setFont(Fonts.t_f);
                            Platform.runLater(() -> forge.cont.addItem(l));
                        }
                    }
                    else {
                        for (String s : GetVersionList.getForgeVersionList(model.id, Launcher.configReader.configModel.downloadServer)) {
                            NewForgeItemModel model2 = new NewForgeItemModel();
                            model2.version = s;

                            ForgeLabel l = new ForgeLabel(model2);
                            l.setText(s.split("-")[0]);
                            l.setFont(Fonts.t_f);
                            Platform.runLater(() -> forge.cont.addItem(l));
                        }
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (OptifineJarModel optiv : GetVersionList.getOptifineVersionList(model.id)){
                        Label l = new Label(optiv.name);
                        l.setFont(Fonts.t_f);
                        Platform.runLater(() -> optifine.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (String fabv : GetVersionList.getFabricVersionList(model.id, Launcher.configReader.configModel.downloadServer)){
                        Label l = new Label(fabv);
                        l.setFont(Fonts.t_f);
                        Platform.runLater(() -> fabric.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (CurseModFileModel fabapav : GetVersionList.getFabricAPIVersionList(model.id, Launcher.configReader.configModel.downloadServer)){
                        CurseFileLabel l = new CurseFileLabel(fabapav.displayName);
                        l.setFont(Fonts.t_f);
                        l.model = fabapav;
                        Platform.runLater(() -> fabricapi.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (CurseModFileModel optfabv : GetVersionList.getOptiFabricVersionList(model.id, Launcher.configReader.configModel.downloadServer)){
                        CurseFileLabel l = new CurseFileLabel(optfabv.displayName);
                        l.setFont(Fonts.t_f);
                        l.model = optfabv;
                        Platform.runLater(() -> optifabric.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (String quiv : GetVersionList.getQuiltVersionList(model.id, Launcher.configReader.configModel.downloadServer)){
                        Label l = new Label(quiv);
                        l.setFont(Fonts.t_f);
                        Platform.runLater(() -> quilt.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            }),
            new LambdaTask(() -> {
                try {
                    for (CurseModFileModel quapv : GetVersionList.getQuiltAPIVersionList(model.id, Launcher.configReader.configModel.downloadServer)){
                        CurseFileLabel l = new CurseFileLabel(quapv.displayName);
                        l.setFont(Fonts.t_f);
                        l.model = quapv;
                        Platform.runLater(() -> quiltapi.cont.addItem(l));
                    }
                }
                catch (Exception ignored){

                }
            })
        ));
        TaskManager.setUpdater((value, mess) -> {});
        TaskManager.addTasks(t);
        TaskManager.execute("<load addons>");

        checkIsNull(forge);
        checkIsNull(optifine);
        checkIsNull(fabric);
        checkIsNull(optifabric);
        checkIsNull(fabricapi);
        checkIsNull(quilt);
        checkIsNull(quiltapi);
    }
    public static class ForgeLabel extends Label {
        public final NewForgeItemModel model;
        public ForgeLabel(NewForgeItemModel model) {
            super(model.version);
            this.model = model;
        }
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
