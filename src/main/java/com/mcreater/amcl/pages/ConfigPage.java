package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.config.ConfigWriter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

import static com.mcreater.amcl.pages.MainPage.configWriter;

public class ConfigPage extends AbstractAnimationPage {
    VBox mainBox;
    JFXButton exit;
    JFXButton cancel;
    Label title;

    HBox box_group;

    Label change_label;
    JFXToggleButton change_set;
    HBox change_box;

    Label wall_label;
    JFXToggleButton wall_set;
    HBox wall_box;

    VBox configs_box;

    Label java_label;
    JFXComboBox<String> java_set;
    JFXButton java_add;
    HBox java_box;
    public ConfigPage(double width,double height,Background bg){
        name = "Config Page";
        this.setBackground(bg);
        set(this);
        in.play();
        this.setAlignment(Pos.TOP_CENTER);

        exit = new JFXButton("Ok");
        exit.setFont(Fonts.s_f);
        exit.setDefaultButton(true);
        exit.setOnAction(event -> Platform.runLater(() -> {
            configWriter.write();
            HelloApplication.setPage(new MainPage(width, height, bg));
        }));

        cancel = new JFXButton("Cancel");
        cancel.setFont(Fonts.s_f);
        cancel.setDefaultButton(true);
        cancel.setOnAction(event -> Platform.runLater(() -> HelloApplication.setPage(new MainPage(width, height, bg))));

        box_group = new HBox();
        box_group.getChildren().addAll(cancel, exit);
        box_group.setAlignment(Pos.TOP_CENTER);

        title = new Label("Settings");
        title.setFont(Fonts.b_f);

        change_set = new JFXToggleButton();
        change_set.setSelected(configWriter.configModel.change_game_dir);
        change_set.selectedProperty().addListener((observable, oldValue, newValue) -> configWriter.configModel.change_game_dir = newValue);

        change_label = new Label("Change Game Dir");
        change_label.setFont(Fonts.t_f);

        change_box = new HBox();
        change_box.getChildren().addAll(change_label, new MainPage.Spacer(), change_set);
        change_box.setAlignment(Pos.CENTER);

        wall_label = new Label("Use Classic Wallpaper");
        wall_label.setFont(Fonts.t_f);

        wall_set = new JFXToggleButton();
        wall_set.setSelected(configWriter.configModel.use_classic_wallpaper);
        wall_set.selectedProperty().addListener((observable, oldValue, newValue) -> configWriter.configModel.use_classic_wallpaper = newValue);

        wall_box = new HBox();
        wall_box.getChildren().addAll(wall_label, new MainPage.Spacer(), wall_set);
        wall_box.setAlignment(Pos.CENTER);

        java_label = new Label("Java Choose");
        java_label.setFont(Fonts.t_f);

        java_set = new JFXComboBox<>();
        load_java_list();
        java_set.setOnAction(event -> {
            configWriter.configModel.selected_java_index = java_set.getValue();
            configWriter.write();
        });


        java_add = new JFXButton("Add Java");
        java_add.setDefaultButton(true);
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Executable", "java.exe"));
            File choosed_path = fileChooser.showOpenDialog(HelloApplication.stage);
            configWriter.configModel.selected_java.add(choosed_path.getPath());
            load_java_list();
        });

        java_box = new HBox();
        java_box.setAlignment(Pos.TOP_CENTER);
        java_box.getChildren().addAll(java_label, new MainPage.Spacer(), java_set, new MainPage.Spacer(), java_add);

        configs_box = new VBox();
        configs_box.setAlignment(Pos.TOP_CENTER);
        configs_box.getChildren().addAll(change_box, wall_box, java_box);

        mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        mainBox.setMinSize(width, height);
        mainBox.setMaxSize(width, height);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.getChildren().addAll(title, configs_box, box_group);
        this.add(mainBox,0,0,1,1);
    }
    public void load_java_list(){
        java_set.getItems().clear();
        java_set.getItems().addAll(configWriter.configModel.selected_java);
        if (configWriter.configModel.selected_java.contains(configWriter.configModel.selected_java_index)){
            java_set.getSelectionModel().select(configWriter.configModel.selected_java.indexOf(configWriter.configModel.selected_java_index));
        }
    }
}
