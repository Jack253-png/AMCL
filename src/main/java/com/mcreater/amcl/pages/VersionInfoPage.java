package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.RemoteMod;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.game.mods.ModHelper;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.RemoveFileToTrash;
import com.mcreater.amcl.util.SetSize;
import com.sun.jna.platform.FileUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.pages.DownloadAddonSelectPage.isValidFileName;
import static com.mcreater.amcl.util.Images.*;

public class VersionInfoPage extends AbstractAnimationPage {
    VBox menu;
    JFXButton mainInfoButton;
    SettingPage last;
    VBox mainBox;
    SettingPage p1;
    VBox b;
    GridPane info;
    ImageView view;
    Label versionname;
    Label fabricversion;
    Label forgeversion;
    Label optiversion;
    Label liteversion;
    SettingPage p2;
    VBox b2;
    JFXButton modsMenu;
    GridPane mods;
    JFXListView<RemoteMod> modList;
    JFXButton addMod;
    JFXButton setted;
    JFXProgressBar bar;
    JFXButton refresh;
    JFXButton delete;
    SettingPage sett;
    JFXButton delVer;
    JFXButton changeName;
    StringItem item;
    public VersionInfoPage(double width, double height){
        super(width, height);
        l = Launcher.MAINPAGE;
        set();

        double t_size = Launcher.barSize;

        menu = new VBox();
        menu.setId("info-menu");
        SetSize.set(menu, this.width / 4, this.height - t_size);
        mainInfoButton = new JFXButton();
        mainInfoButton.setFont(Fonts.s_f);
        SetSize.setWidth(mainInfoButton, this.width / 4);
        mainInfoButton.setOnAction(event -> {
            setP1(p1);
            setType(mainInfoButton);
        });
        modsMenu = new JFXButton();
        modsMenu.setFont(Fonts.s_f);
        SetSize.setWidth(modsMenu, this.width / 4);
        modsMenu.setOnAction(event -> {
            setP1(p2);
            setType(modsMenu);
        });
        menu.getChildren().addAll(mainInfoButton, modsMenu);

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
        info.setHgap(20);
        info.setVgap(20);
        info.add(view, 0, 0, 1, 1);
        info.add(versionname, 1, 0, 1, 1);
        info.add(fabricversion, 1, 1, 1, 1);
        info.add(forgeversion, 1, 2, 1, 1);
        info.add(liteversion, 1, 3, 1, 1);
        info.add(optiversion, 1, 4, 1, 1);
        delVer = new JFXButton();
        SetSize.set(delVer, t_size, t_size);
        delVer.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding(this::returnBlack), t_size, t_size));
        delVer.setOnAction(event -> {
            RemoveFileToTrash.remove(LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s", Launcher.configReader.configModel.selected_version_index)));
            Launcher.setPage(Launcher.MAINPAGE, this);
        });
        item = new StringItem("", this.width / 4 * 3);
        item.cont.setFont(Fonts.t_f);
        changeName = new JFXButton();
        changeName.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(this::returnBlack), t_size, t_size));
        SetSize.set(changeName, t_size, t_size);
        changeName.setOnAction(event -> {
            if (isValidFileName(item.cont.getText())) {
                String versionDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s", item.cont.getText()));
                if (new File(versionDir).exists()){
                    FastInfomation.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.1"), "");
                }
                else {
                    String temp = Launcher.configReader.configModel.selected_version_index;
                    Launcher.configReader.configModel.selected_version_index = item.cont.getText();
                    String dir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s", temp));
                    String newDir = LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions\\%s", item.cont.getText()));
                    String jar = newDir + String.format("\\%s.jar", temp);
                    String json = newDir + String.format("\\%s.json", temp);
                    String newJar = newDir + String.format("\\%s.jar", item.cont.getText());
                    String newJson = newDir + String.format("\\%s.json", item.cont.getText());
                    new File(dir).renameTo(new File(newDir));
                    new File(jar).renameTo(new File(newJar));
                    new File(json).renameTo(new File(newJson));
                    Launcher.setPage(Launcher.MAINPAGE, this);
                }
            }
            else{
                FastInfomation.create(Launcher.languageManager.get("ui.install.nameInvaild.title"), Launcher.languageManager.get("ui.install.nameInvaild.2"), "");
            }
        });
        HBox b1 = new HBox(delVer, changeName);
        b.getChildren().addAll(info, item, b1);
        p1 = new SettingPage(this.width / 4 * 3, this.height - t_size, b);

        b2 = new VBox();
        mods = new GridPane();
        modList = new JFXListView<>();
        SetSize.set(modList, this.width / 4 * 3, this.height - t_size * 2 - 10);

        addMod = new JFXButton();
        SetSize.set(addMod, t_size, t_size);
        addMod.setGraphic(Launcher.getSVGManager().plus(Bindings.createObjectBinding(this::returnBlack), t_size, t_size));
        addMod.setOnAction(event -> Launcher.setPage(Launcher.ADDMODSPAGE, this));

        refresh = new JFXButton();
        SetSize.set(refresh, t_size, t_size);
        refresh.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(this::returnBlack), t_size, t_size));
        refresh.setOnAction(actionEvent -> new Thread(this::loadMods).start());

        delete = new JFXButton();
        SetSize.set(delete, t_size, t_size);
        delete.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding(this::returnBlack), t_size, t_size));
        delete.setOnAction(event -> {
            ProcessDialog dialog = new ProcessDialog(1, String.format(Launcher.languageManager.get("ui.versioninfopage.deletemod.deleteing.title"), modList.getSelectionModel().getSelectedItem().name.getText()));
            dialog.Create();
            dialog.setV(0, 50, modList.getSelectionModel().getSelectedItem().name.getText());
            new Thread(() -> {
                String path = modList.getSelectionModel().getSelectedItem().path;
                RemoveFileToTrash.remove(path);
                Platform.runLater(dialog::close);
            }).start();
            new Thread(this::loadMods).start();

        });

        modList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<RemoteMod>) c -> delete.setDisable(modList.getSelectionModel().getSelectedItem() == null));

        bar = new JFXProgressBar(-1.0D);
        SetSize.setWidth(bar, this.width / 4 * 3);
        mods.add(addMod, 0, 0, 1, 1);
        mods.add(refresh, 1, 0, 1, 1);
        mods.add(delete, 2, 0, 1, 1);
        mods.add(bar, 0, 1, 3, 1);
        mods.add(modList, 0, 2, 3, 1);
        b2.getChildren().addAll(mods);

        p2 = new SettingPage(this.width / 4 * 3, this.height - t_size, b2);
        p2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setP1(p1);
        setType(mainInfoButton);
    }
    public void setByview(){
        String type;
        try {
            type = versionTypeGetter.get(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        versionname.setText(Launcher.configReader.configModel.selected_version_index);
        item.cont.setText(Launcher.configReader.configModel.selected_version_index);
        view.setFitWidth(40);
        view.setFitHeight(40);
        switch (type) {
            case "original":
                view.setImage(original);
                break;
            case "forge":
            case "forge-optifine":
                view.setImage(forge);
                break;
            case "fabric":
                view.setImage(fabric);
                break;
            case "liteloader":
                view.setImage(liteloader);
                break;
            case "optifine":
                view.setImage(optifine);
                break;
        }
        fabricversion.setText(versionTypeGetter.getFabricVersion(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index));
        forgeversion.setText(versionTypeGetter.getForgeVersion(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index));
        optiversion.setText(versionTypeGetter.getOptifineVersion(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index));
        liteversion.setText(versionTypeGetter.getLiteLoaderVersion(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index));
    }
    public void setType(JFXButton b){
        setted = b;
        b.setCursor(Cursor.HAND);
        for (Node bs : menu.getChildren()){
            bs.setDisable(bs == b);
        }
    }
    public void setP1(SettingPage p){
        sett = p;
        if (last != null) {
            last.setOut();
        }
        last = p;
        last.setIn();
        last.setTypeAll(true);
        last.in.stop();
        last.setTypeAll(false);
        this.getChildren().clear();
        mainBox = new VBox();
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.getChildren().addAll(p);
        SetSize.set(mainBox, this.width / 4 * 3, this.height - Launcher.barSize);
        this.add(menu, 0, 0, 1, 1);
        this.add(mainBox, 1, 0, 1, 1);
        if (p == p2){
            p.setDisable(!ModHelper.isModded(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index));
            if (p.isDisabled()) {
                FastInfomation.create(Launcher.languageManager.get("ui.versioninfopage.unModded.title"), Launcher.languageManager.get("ui.versioninfopage.unModded.content"), "");
            }
        }
    }

    public void setType(boolean b){
        modList.setDisable(b);
        addMod.setDisable(b);
        refresh.setDisable(b);
    }
    public void loadMods(){
        delete.setDisable(true);
        try {
            Platform.runLater(() -> setP1(sett));
            Platform.runLater(() -> bar.setProgress(-1.0D));
            Platform.runLater(modList.getItems()::clear);
            Platform.runLater(() -> setType(true));
            Vector<File> f = ModHelper.getMod(Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index);
            for (File file : f) {
                CommonModInfoModel model = ModHelper.getModInfo(file.getPath());
                if (!Objects.equals(model.name, "")) {
                    RemoteMod m = new RemoteMod(model);
                    Platform.runLater(() -> modList.getItems().add(m));
                    double d = (double) modList.getItems().size() / (double) f.size();
                    double lat = bar.getProgress();
                    for (int i = 0; i < 100; i++) {
                        int finalI = i;
                        Platform.runLater(() -> bar.setProgress(lat + (d - lat) / 100 * finalI));
                    }
                    Platform.runLater(() -> bar.setProgress(d));
                }
            }
            Platform.runLater(() -> bar.setProgress(1.0D));
            sleep(50);
            Platform.runLater(() -> setType(false));
        }
        catch (IOException e){
        }
        finally {
            setType(setted);
            delete.setDisable(true);
        }
    }
    public void sleep(long l){
        try {Thread.sleep(l);}
        catch (InterruptedException ignored){}
    }
    public void refresh() {
        Platform.runLater(this::setByview);
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
        setType(setted);
        loadMods();
    }

    public void refreshLanguage() {
        name = Launcher.languageManager.get("ui.versioninfopage.name");
        mainInfoButton.setText(Launcher.languageManager.get("ui.versioninfopage.menu._01"));
        modsMenu.setText(Launcher.languageManager.get("ui.versioninfopage.menu._02"));
        item.title.setText(Launcher.languageManager.get("ui.versioninfopage.item.name"));
    }

    public void refreshType() {

    }
    
    public void onExitPage() {

    }
}

