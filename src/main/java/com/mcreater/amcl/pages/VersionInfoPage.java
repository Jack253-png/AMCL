package com.mcreater.amcl.pages;

import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VersionInfoPage extends AbstractAnimationPage {
    VBox mainBox;
    public VersionInfoPage(double width, double height){
        super(width, height);
        l = HelloApplication.MAINPAGE;
        set();
        mainBox = new VBox();
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setMinSize(this.width, this.height);
        mainBox.setMaxSize(this.width, this.height);
        mainBox.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        this.add(new HBox(), 0, 0, 1, 1);
    }
    public void refresh() {

    }

    public void refreshLanguage() {
        name = HelloApplication.languageManager.get("ui.versioninfopage.name");
    }

    public void refreshType() {

    }
}
