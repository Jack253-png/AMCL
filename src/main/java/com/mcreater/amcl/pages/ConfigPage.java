package com.mcreater.amcl.pages;

import com.jfoenix.controls.*;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.JavaInfoGetter;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Vector;

public class ConfigPage extends AbstractAnimationPage {
    VBox mainBox;
    JFXButton exit;
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
    JFXButton java_get;
    HBox java_box;

    Label mem_label;
    Slider max_mem;
    GridPane mem_pane;

    int w;
    int h;
    public ConfigPage(int width, int height, Background bg){
        l = HelloApplication.MAINPAGE;
        w = width;
        h = height;
        this.setBackground(bg);
        set();
        this.setAlignment(Pos.TOP_CENTER);

        title = new Label("Settings");
        title.setFont(Fonts.b_f);

        change_set = new JFXToggleButton();
        change_set.setSelected(HelloApplication.configReader.configModel.change_game_dir);
        change_set.selectedProperty().addListener((observable, oldValue, newValue) -> HelloApplication.configReader.configModel.change_game_dir = newValue);

        change_label = new Label("Change Game Dir");
        change_label.setFont(Fonts.t_f);

        change_box = new HBox();
        change_box.getChildren().addAll(change_label, new MainPage.Spacer(), change_set);
        change_box.setAlignment(Pos.CENTER);

        wall_label = new Label("Use Classic Wallpaper");
        wall_label.setFont(Fonts.t_f);

        wall_set = new JFXToggleButton();
        wall_set.setSelected(HelloApplication.configReader.configModel.use_classic_wallpaper);
        wall_set.selectedProperty().addListener((observable, oldValue, newValue) -> HelloApplication.configReader.configModel.use_classic_wallpaper = newValue);

        wall_box = new HBox();
        wall_box.getChildren().addAll(wall_label, new MainPage.Spacer(), wall_set);
        wall_box.setAlignment(Pos.CENTER);

        mem_label = new Label("Memory");
        mem_label.setFont(Fonts.t_f);

        max_mem = new JFXSlider(256, 4096, HelloApplication.configReader.configModel.max_memory);
        max_mem.setShowTickLabels(true);
        max_mem.setShowTickMarks(true);
        max_mem.setMajorTickUnit(128);
        max_mem.setMinorTickCount(128);
        max_mem.setOrientation(Orientation.HORIZONTAL);
        max_mem.setMaxSize(400, 50);
        max_mem.setMinSize(400, 50);
        max_mem.valueProperty().addListener((observable, oldValue, newValue) -> setMem(newValue));

        mem_pane = new GridPane();
        mem_pane.setAlignment(Pos.CENTER);
        mem_pane.add(mem_label, 0, 0, 1, 1);
        mem_pane.add(max_mem, 1, 0, 1, 1);

        java_label = new Label("Java Choose");
        java_label.setFont(Fonts.t_f);

        java_set = new JFXComboBox<>();
        load_java_list();
        java_set.setOnAction(event -> {
            HelloApplication.configReader.configModel.selected_java_index = java_set.getValue();
            HelloApplication.configReader.write();
        });


        java_add = new JFXButton("Add");
        java_add.setDefaultButton(true);
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Executable", "java.exe"));
            File choosed_path = fileChooser.showOpenDialog(HelloApplication.stage);
            if (choosed_path != null) {
                HelloApplication.configReader.configModel.selected_java.add(choosed_path.getPath());
                load_java_list();
            }
        });

        java_get = new JFXButton("Get");
        java_get.setDefaultButton(true);
        java_get.setFont(Fonts.t_f);
        java_get.setOnAction(event -> new Thread(() -> Platform.runLater(() -> {
            if (HelloApplication.configReader.configModel.selected_java.contains(HelloApplication.configReader.configModel.selected_java_index) && new File(HelloApplication.configReader.configModel.selected_java_index).exists()) {
                Vector<String> info = JavaInfoGetter.get(new File(HelloApplication.configReader.configModel.selected_java_index));
                FastInfomation.create("Java Info", "Java Info : ", "Version = "+info.get(0)+"\nBits = "+info.get(1)+"\nCompany = "+info.get(2)+"\nType = "+info.get(3));


            }
            else{
                FastInfomation.create("Java Select", "Please select a Java executable", "");
            }
        })).start());

        java_box = new HBox();
        java_box.setAlignment(Pos.TOP_CENTER);
        java_box.getChildren().addAll(java_label, new MainPage.Spacer(), java_set, new MainPage.Spacer(), java_get, new MainPage.Spacer(), java_add);

        exit = new JFXButton("Ok");
        exit.setFont(Fonts.s_f);
        exit.setDefaultButton(true);
        exit.setOnAction(event -> Platform.runLater(() -> {
            if (getCanMovePage()) {
                HelloApplication.configReader.write();
                HelloApplication.setPage(HelloApplication.MAINPAGE);
            }
        }));

        box_group = new HBox();
        box_group.getChildren().addAll(exit);
        box_group.setAlignment(Pos.TOP_CENTER);

        configs_box = new VBox();
        configs_box.setSpacing(1);
        configs_box.setAlignment(Pos.TOP_CENTER);
        configs_box.getChildren().addAll(change_box, wall_box, java_box, mem_pane);

        java_get.setButtonType(JFXButton.ButtonType.RAISED);
        java_add.setButtonType(JFXButton.ButtonType.RAISED);
        exit.setButtonType(JFXButton.ButtonType.RAISED);

        mainBox = new VBox();
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.getChildren().addAll(title, configs_box, box_group);
        mainBox.setMinSize(w, h);
        mainBox.setMaxSize(w, h);
        mainBox.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        this.add(mainBox,0,1,1,1);
    }
    public void load_java_list(){
        java_set.getItems().clear();
        java_set.getItems().addAll(HelloApplication.configReader.configModel.selected_java);
        if (HelloApplication.configReader.configModel.selected_java.contains(HelloApplication.configReader.configModel.selected_java_index)){
            java_set.getSelectionModel().select(HelloApplication.configReader.configModel.selected_java.indexOf(HelloApplication.configReader.configModel.selected_java_index));
        }
    }
    public void refresh(){
        this.setMinSize(w, h);
        this.setMaxSize(w, h);
    }
    public void refreshLanguage(){
        name = HelloApplication.languageManager.get("ui.configpage.name");
    }
    public void setMem(Number mem){
        HelloApplication.configReader.configModel.max_memory = mem.intValue();
        HelloApplication.configReader.write();
    }
}
