package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.LocalMod;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.game.mods.ModHelper;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.FileUtils.RemoveFileToTrash;
import com.mcreater.amcl.util.J8Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;
import static com.mcreater.amcl.pages.DownloadAddonSelectPage.isValidFileName;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class VersionInfoPage extends AbstractMenuBarPage {
    public Logger logger = LogManager.getLogger(VersionInfoPage.class);
    public JFXButton mainInfoButton;
    public AdvancedScrollPane p1;
    public VBox b;
    public GridPane info;
    public ImageView view;
    public Label versionname;
    public Label fabricversion;
    public Label forgeversion;
    public Label optiversion;
    public Label liteversion;
    public Label quiltversion;
    public AdvancedScrollPane p2;
    public VBox b2;
    public JFXButton modsMenu;
    public GridPane mods;
    public SmoothableListView<LocalMod> modList;
    public JFXButton addMod;
    public JFXButton setted;
    public JFXButton refresh;
    public JFXButton delete;
    public JFXButton delVer;
    public JFXButton changeName;
    public StringItem item;
    public boolean modLoaded = false;
    private Thread modLoadThread;

    public VersionInfoPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;

        double t_size = Launcher.barSize;

        mainInfoButton = new JFXButton();
        mainInfoButton.setFont(Fonts.s_f);
        FXUtils.ControlSize.setWidth(mainInfoButton, this.width / 4);
        mainInfoButton.setOnAction(event -> this.setP1(0));
        modsMenu = new JFXButton();
        modsMenu.setFont(Fonts.s_f);
        FXUtils.ControlSize.setWidth(modsMenu, this.width / 4);
        modsMenu.setOnAction(event -> {
            if (!VersionTypeGetter.modded(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index)) {
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.versioninfopage.unModded.title"), Launcher.languageManager.get("ui.versioninfopage.unModded.content"), "");
            } else {
                this.setP1(1);
            }
        });

        b = new VBox();
        info = new GridPane();
        view = new ImageView();
        versionname = new Label();
        versionname.setFont(Fonts.s_f);
        fabricversion = new Label();
        fabricversion.setFont(Fonts.t_f);
        forgeversion = new Label();
        forgeversion.setFont(Fonts.t_f);
        optiversion = new Label();
        optiversion.setFont(Fonts.t_f);
        liteversion = new Label();
        liteversion.setFont(Fonts.t_f);
        quiltversion = new Label();
        quiltversion.setFont(Fonts.t_f);
        info.setHgap(20);
        info.setVgap(20);
        info.add(view, 0, 0, 1, 1);
        info.add(versionname, 1, 0, 1, 1);
        info.add(fabricversion, 1, 1, 1, 1);
        info.add(forgeversion, 1, 2, 1, 1);
        info.add(liteversion, 1, 3, 1, 1);
        info.add(optiversion, 1, 4, 1, 1);
        info.add(quiltversion, 1, 5, 1, 1);
        delVer = new JFXButton();
        FXUtils.ControlSize.set(delVer, t_size, t_size);
        delVer.setOnAction(event -> {
            RemoveFileToTrash.remove(LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s"), Launcher.configReader.configModel.selected_version_index)));
            Launcher.setPage(Launcher.MAINPAGE, this);
        });
        item = new StringItem("", this.width / 4 * 3);
        changeName = new JFXButton();
        FXUtils.ControlSize.set(changeName, t_size, t_size);
        changeName.setOnAction(event -> {
            if (isValidFileName(item.cont.getText())) {
                String versionDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s"), item.cont.getText()));
                if (new File(versionDir).exists()) {
                    SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.1"), "");
                } else {
                    String temp = Launcher.configReader.configModel.selected_version_index;
                    Launcher.configReader.configModel.selected_version_index = item.cont.getText();
                    String dir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s"), temp));
                    String newDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format(buildPath("versions", "%s"), item.cont.getText()));

                    String jar = String.format(buildPath("%s", "%s.jar"), newDir, temp);
                    String json = String.format(buildPath("%s", "%s.json"), newDir, temp);
                    String newJar = String.format(buildPath("%s", "%s.jar"), newDir, item.cont.getText());
                    String newJson = String.format(buildPath("%s", "%s.json"), newDir, item.cont.getText());

                    new File(dir).renameTo(new File(newDir));
                    new File(jar).renameTo(new File(newJar));
                    new File(json).renameTo(new File(newJson));
                    Launcher.setPage(Launcher.MAINPAGE, this);
                }
            } else {
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.2"), "");
            }
        });
        HBox b1 = new HBox(delVer, changeName);
        b.getChildren().addAll(info, item, b1);
        p1 = new AdvancedScrollPane(this.width / 4 * 3, this.height - t_size, b);

        b2 = new VBox();
        mods = new GridPane();
        modList = new SmoothableListView<>(this.width / 4 * 3, this.height - t_size * 2);

        addMod = new JFXButton();
        FXUtils.ControlSize.set(addMod, t_size, t_size);
        addMod.setOnAction(event -> Launcher.setPage(Launcher.ADDMODSPAGE, this));

        refresh = new JFXButton();
        FXUtils.ControlSize.set(refresh, t_size, t_size);
        refresh.setOnAction(action -> startLoadingMods(() -> {
        }));

        delete = new JFXButton();
        FXUtils.ControlSize.set(delete, t_size, t_size);

        delete.setOnAction(event -> {
            ProcessDialog dialog = new ProcessDialog(1, Launcher.languageManager.get("ui.versioninfopage.deletemod.deleteing.title", modList.selectedItem.name.getText()));
            dialog.Create();
            dialog.setV(0, 50, modList.selectedItem.name.getText());
            new Thread(() -> {
                String path = modList.selectedItem.path;
                RemoveFileToTrash.remove(path);
                Platform.runLater(dialog::close);
                Platform.runLater(() -> refresh.getOnAction().handle(new ActionEvent()));
            }).start();
        });

        modList.setOnAction(() -> {
            delete.setDisable(modList.selectedItem == null);
            
        });

        mods.add(addMod, 0, 1, 1, 1);
        mods.add(refresh, 1, 1, 1, 1);
        mods.add(delete, 2, 1, 1, 1);
        mods.add(modList.page, 0, 2, 3, 1);
        b2.getChildren().addAll(mods);

        p2 = new AdvancedScrollPane(this.width / 4 * 3, this.height - t_size, b2);

        super.addNewPair(new ImmutablePair<>(mainInfoButton, p1));
        super.addNewPair(new ImmutablePair<>(modsMenu, p2));
        super.setOnAction(i -> {
            setted = super.menubuttons.get(i);
            setType(setted);
        });
        super.setP1(0);
        super.setButtonType(JFXButton.ButtonType.RAISED);
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

    public void loadVersionType() {
        VersionTypeGetter.VersionType type;
        try {
            type = VersionTypeGetter.get(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        versionname.setText(Launcher.configReader.configModel.selected_version_index);
        item.cont.setText(Launcher.configReader.configModel.selected_version_index);
        view.setFitWidth(40);
        view.setFitHeight(40);
        view.setImage(VersionTypeGetter.VersionType.getImage(type));

        String fabVer = VersionTypeGetter.getFabricVersionSrc(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        String forVer = VersionTypeGetter.getForgeVersionSrc(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        String optVer = VersionTypeGetter.getOptifineVersionSrc(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        String litVer = VersionTypeGetter.getLiteLoaderVersionSrc(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        String quiVer = VersionTypeGetter.getQuiltVersionSrc(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);

        fabricversion.setText(fabVer == null ? Launcher.languageManager.get("ui.versioninfopage.noFabric") : Launcher.languageManager.get("ui.versioninfopage.hasfabric", fabVer));
        forgeversion.setText(forVer == null ? Launcher.languageManager.get("ui.versioninfopage.noForge") : Launcher.languageManager.get("ui.versioninfopage.hasforge", forVer));
        optiversion.setText(optVer == null ? Launcher.languageManager.get("ui.versioninfopage.noOptifine") : Launcher.languageManager.get("ui.versioninfopage.hasoptifine", optVer));
        liteversion.setText(litVer == null ? Launcher.languageManager.get("ui.versioninfopage.noLiteloader") : Launcher.languageManager.get("ui.versioninfopage.hasliteloader", litVer));
        quiltversion.setText(quiVer == null ? Launcher.languageManager.get("ui.versioninfopage.noQuilt") : Launcher.languageManager.get("ui.versioninfopage.hasquilt", quiVer));
    }

    public void setType(boolean b) {
        modList.setDisable(b);
        refresh.setDisable(b);
    }

    public void startLoadingMods(Runnable finish) {
        if (modLoadThread != null) modLoadThread.stop();
        modLoadThread = new Thread(() -> {
            loadMods();
            finish.run();
        });
        modLoadThread.start();
    }

    public void loadMods() {
        delete.setDisable(true);
        try {
            Platform.runLater(modList::clear);
            Platform.runLater(() -> setType(true));
            ModHelper.getMod(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index)
                    .stream()
                    .flatMap((Function<File, Stream<CommonModInfoModel>>) file -> {
                        logger.info("processing mod " + file);
                        return ModHelper.getModInfo(file).stream();
                    })
                    .forEach(commonModInfoModel -> {
                        try {
                            Platform.runLater(() -> modList.addItem(new LocalMod(commonModInfoModel)));
                        } catch (Exception e) {
                            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.versioninfopage.mod.load.fail"));
                        }
                    });

            Platform.runLater(() -> setType(false));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setType(setted);
            delete.setDisable(true);
        }
    }

    public void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ignored) {
        }
    }

    public void refresh() {
        Platform.runLater(this::loadVersionType);
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
        setType(setted);
        if (!modLoaded) {
            startLoadingMods(() -> modLoaded = true);
        }
    }

    public void refreshLanguage() {
        name = Launcher.languageManager.get("ui.versioninfopage.name");
        mainInfoButton.setText(Launcher.languageManager.get("ui.versioninfopage.menu._01"));
        modsMenu.setText(Launcher.languageManager.get("ui.versioninfopage.menu._02"));
        item.title.setText(Launcher.languageManager.get("ui.versioninfopage.item.name"));
    }

    public void refreshType() {
        double t_size = Launcher.barSize;
        delVer.setGraphic(Launcher.getSVGManager().delete(ThemeManager.createPaintBinding(), t_size, t_size));
        changeName.setGraphic(Launcher.getSVGManager().refresh(ThemeManager.createPaintBinding(), t_size, t_size));
        addMod.setGraphic(Launcher.getSVGManager().plus(ThemeManager.createPaintBinding(), t_size, t_size));
        refresh.setGraphic(Launcher.getSVGManager().refresh(ThemeManager.createPaintBinding(), t_size, t_size));
        delete.setGraphic(Launcher.getSVGManager().delete(ThemeManager.createPaintBinding(), t_size, t_size));
    }

    public void onExitPage() {
        super.setP1(0);
        modLoadThread.stop();
    }
}

