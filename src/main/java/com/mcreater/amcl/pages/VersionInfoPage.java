package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.controls.RemoteMod;
import com.mcreater.amcl.game.mods.ModHelper;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.FinalSVGs.*;
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
    SettingPage sett;
    public VersionInfoPage(double width, double height){
        super(width, height);
        l = Application.MAINPAGE;
        set();

        double t_size = Application.barSize;

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
        b.getChildren().addAll(info);
        p1 = new SettingPage(this.width / 4 * 3, this.height - t_size, b);

        b2 = new VBox();
        mods = new GridPane();
        modList = new JFXListView<>();
        SetSize.set(modList, this.width / 4 * 3, this.height - t_size * 2 - 10);

        addMod = new JFXButton();
        SetSize.set(addMod, t_size, t_size);
        addMod.setGraphic(addNode);
        addMod.setOnAction(event -> Application.setPage(Application.ADDMODSPAGE, this));

        refresh = new JFXButton();
        SetSize.set(refresh, t_size, t_size);
        refresh.setGraphic(refreshNode);
        refresh.setOnAction(actionEvent -> new Thread(this::loadMods).start());

        bar = new JFXProgressBar(-1.0D);
        SetSize.setWidth(bar, this.width / 4 * 3);
        mods.add(addMod, 0, 0, 1, 1);
        mods.add(refresh, 1, 0, 1, 1);
        mods.add(bar, 0, 1, 2, 1);
        mods.add(modList, 0, 2, 2, 1);
        b2.getChildren().addAll(mods);

        p2 = new SettingPage(this.width / 4 * 3, this.height - t_size, b2);
        p2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setP1(p1);
        setType(mainInfoButton);
    }
    public void setByview(){
        String type;
        try {
            type = versionTypeGetter.get(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index);
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        versionname.setText(Application.configReader.configModel.selected_version_index);
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
        fabricversion.setText(versionTypeGetter.getFabricVersion(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index));
        forgeversion.setText(versionTypeGetter.getForgeVersion(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index));
        optiversion.setText(versionTypeGetter.getOptifineVersion(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index));
        liteversion.setText(versionTypeGetter.getLiteLoaderVersion(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index));
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
        SetSize.set(mainBox, this.width / 4 * 3, this.height - Application.barSize);
        this.add(menu, 0, 0, 1, 1);
        this.add(mainBox, 1, 0, 1, 1);
        if (p == p2){
            p.setDisable(!ModHelper.isModded(Application.configReader.configModel.selected_minecraft_dir_index, Application.configReader.configModel.selected_version_index));
            if (p.isDisabled()) {
                FastInfomation.create(Application.languageManager.get("ui.versioninfopage.unModded.title"), Application.languageManager.get("ui.versioninfopage.unModded.content"), "");
            }
        }
    }

    public void setType(boolean b){
        modList.setDisable(b);
        addMod.setDisable(b);
        refresh.setDisable(b);
    }
    public void loadMods(){
        Platform.runLater(() -> setP1(sett));
        Platform.runLater(() -> bar.setProgress(-1.0D));
        Platform.runLater(modList.getItems()::clear);
        Platform.runLater(() -> setType(true));
        Vector<File> f = ModHelper.getMod(Application.configReader.configModel.selected_minecraft_dir_index,Application.configReader.configModel.selected_version_index);
        for (File file : f){
            CommonModInfoModel model = ModHelper.getModInfo(file.getPath());
            if (!Objects.equals(model.name, "")) {
                RemoteMod m = new RemoteMod(model);
                Platform.runLater(() -> modList.getItems().add(m));
                double d = (double) modList.getItems().size() / (double) f.size();
                Platform.runLater(() -> bar.setProgress(d));
                sleep(100);
            }
        }
        Platform.runLater(() -> bar.setProgress(1.0D));
        sleep(50);
        Platform.runLater(() -> setType(false));
        setType(setted);
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
        name = Application.languageManager.get("ui.versioninfopage.name");
        mainInfoButton.setText(Application.languageManager.get("ui.versioninfopage.menu._01"));
        modsMenu.setText(Application.languageManager.get("ui.versioninfopage.menu._02"));
    }

    public void refreshType() {

    }
    
    public void onExitPage() {

    }
}

