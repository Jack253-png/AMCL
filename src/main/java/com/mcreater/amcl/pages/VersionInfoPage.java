package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.setSize;
import javafx.scene.layout.VBox;

public class VersionInfoPage extends AbstractAnimationPage {
    VBox toolBox;
    JFXButton mainInfoButton;
    public VersionInfoPage(double width, double height){
        super(width, height);
        l = HelloApplication.MAINPAGE;
        set();

        toolBox = new VBox();
        toolBox.setStyle("-fx-background-color : rgba(255, 255, 255, 0.75)");
        setSize.set(toolBox, this.width / 4, this.height);
        mainInfoButton = new JFXButton("00000");
        mainInfoButton.setFont(Fonts.s_f);
        mainInfoButton.setStyle("-fx-background-radius:25;-fx-border-radius:25");
        setSize.setWidth(mainInfoButton, this.width / 4);
        toolBox.getChildren().add(mainInfoButton);

        this.add(toolBox, 0, 0, 1, 1);
    }
    public void refresh() {

    }

    public void refreshLanguage() {
        name = HelloApplication.languageManager.get("ui.versioninfopage.name");
    }

    public void refreshType() {

    }
    
    public void onExitPage() {

    }
}

