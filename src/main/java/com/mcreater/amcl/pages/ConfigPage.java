package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.JavaInfoGetter;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.tools.JavaCompiler;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class ConfigPage extends AbstractAnimationPage {
    VBox mainBox;
    JFXButton exit;
    Label title;

    HBox box_group;

    Label change_label;
    JFXToggleButton change_set;
    HBox change_box;

    VBox configs_box;

    Label java_label;
    JFXComboBox<String> java_set;
    JFXButton java_add;
    JFXButton java_get;
    HBox java_box;

    Label mem_label;
    Slider max_mem;
    GridPane mem_pane;

    Label lang_label;
    JFXComboBox<String> lang_set;
    HBox lang_box;

    Map<String, String> langs;

    int w;
    int h;
    public ConfigPage(int width, int height){
        l = HelloApplication.MAINPAGE;
        w = width;
        h = height;
        set();
        this.setAlignment(Pos.TOP_CENTER);

        langs = new TreeMap<>();
        langs.put("简体中文", "CHINESE");
        langs.put("English(US)", "ENGLISH");

        title = new Label();
        title.setFont(Fonts.b_f);

        change_set = new JFXToggleButton();
        change_set.setSelected(HelloApplication.configReader.configModel.change_game_dir);
        change_set.selectedProperty().addListener((observable, oldValue, newValue) -> HelloApplication.configReader.configModel.change_game_dir = newValue);

        change_label = new Label();
        change_label.setFont(Fonts.t_f);

        change_box = new HBox();
        change_box.getChildren().addAll(change_label, new MainPage.Spacer(), change_set);
        change_box.setAlignment(Pos.CENTER);

        mem_label = new Label();
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

        java_label = new Label();
        java_label.setFont(Fonts.t_f);

        java_set = new JFXComboBox<>();
        load_java_list();
        java_set.setOnAction(event -> {
            HelloApplication.configReader.configModel.selected_java_index = java_set.getValue();
            HelloApplication.configReader.write();
        });

        java_add = new JFXButton();
        java_add.setDefaultButton(true);
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(HelloApplication.languageManager.get("ui.configpage.java_choose.title"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(HelloApplication.languageManager.get("ui.configpage.java_choose.filename.description"), "java.exe"));
            File choosed_path = fileChooser.showOpenDialog(HelloApplication.stage);
            if (choosed_path != null) {
                HelloApplication.configReader.configModel.selected_java.add(choosed_path.getPath());
                load_java_list();
            }
        });

        java_get = new JFXButton(HelloApplication.languageManager.get("ui.configpage.java_get.name"));
        java_get.setDefaultButton(true);
        java_get.setFont(Fonts.t_f);
        java_get.setOnAction(event -> new Thread(() -> Platform.runLater(() -> {
            if (HelloApplication.configReader.configModel.selected_java.contains(HelloApplication.configReader.configModel.selected_java_index) && new File(HelloApplication.configReader.configModel.selected_java_index).exists()) {
                Vector<String> v = null;
                try {
                    v = JavaInfoGetter.get(new File(HelloApplication.configReader.configModel.selected_java_index));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                FastInfomation.create(HelloApplication.languageManager.get("ui.configpage.java_info.title"), String.format(HelloApplication.languageManager.get("ui.configpage.java_info.Headercontent"), v.get(0), v.get(1), v.get(2), v.get(3)), "");
            }
            else{
                FastInfomation.create(HelloApplication.languageManager.get("ui.configpage.select_java.title"), HelloApplication.languageManager.get("ui.configpage.select_java.Headercontent"), "");
            }
        })).start());

        java_box = new HBox();
        java_box.setAlignment(Pos.TOP_CENTER);
        java_box.getChildren().addAll(java_label, new MainPage.Spacer(), java_set, new MainPage.Spacer(), java_get, new MainPage.Spacer(), java_add);

        exit = new JFXButton();
        exit.setFont(Fonts.s_f);
        exit.setDefaultButton(true);
        exit.setOnAction(event -> Platform.runLater(() -> {
            HelloApplication.configReader.write();
            HelloApplication.setPage(HelloApplication.MAINPAGE, this);
        }));

        box_group = new HBox();
        box_group.getChildren().addAll(exit);
        box_group.setAlignment(Pos.TOP_CENTER);

        lang_label = new Label();
        lang_label.setFont(Fonts.t_f);

        lang_set = new JFXComboBox<>();
        for (Map.Entry<String, String> entry : langs.entrySet()) {
            lang_set.getItems().add(entry.getKey());
        }
        lang_set.getSelectionModel().select(getKey(langs, HelloApplication.configReader.configModel.language));
        lang_set.setOnAction(event -> {
            HelloApplication.configReader.configModel.language = langs.get(lang_set.getValue());
            HelloApplication.configReader.write();
            HelloApplication.languageManager.setLanguage(LanguageManager.valueOf(HelloApplication.configReader.configModel.language));
            refreshLanguage();
            HelloApplication.setAllPage(this);
        });

        lang_box = new HBox();
        lang_box.setAlignment(Pos.TOP_CENTER);
        lang_box.getChildren().addAll(lang_label, new MainPage.Spacer(), lang_set);

        configs_box = new VBox();
        configs_box.setSpacing(1);
        configs_box.setAlignment(Pos.TOP_CENTER);
        configs_box.getChildren().addAll(change_box, java_box, mem_pane, lang_box);

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
        if (HelloApplication.configReader.configModel.selected_java.contains(HelloApplication.configReader.configModel.selected_java_index) || new File(HelloApplication.configReader.configModel.selected_java_index).exists()){
            java_set.getSelectionModel().select(HelloApplication.configReader.configModel.selected_java.indexOf(HelloApplication.configReader.configModel.selected_java_index));
        }
        else{
            java_set.getSelectionModel().select("");
        }
    }
    public void refresh(){
        this.setMinSize(w, h);
        this.setMaxSize(w, h);
    }
    public void refreshLanguage(){
        name = HelloApplication.languageManager.get("ui.configpage.name");
        title.setText(HelloApplication.languageManager.get("ui.configpage.title.name"));
        change_label.setText(HelloApplication.languageManager.get("ui.configpage.change_label.name"));
        mem_label.setText(HelloApplication.languageManager.get("ui.configpage.mem_label.name"));
        java_label.setText(HelloApplication.languageManager.get("ui.configpage.java_label.name"));
        java_add.setText(HelloApplication.languageManager.get("ui.configpage.java_add.name"));
        exit.setText(HelloApplication.languageManager.get("ui.configpage.exit.name"));
        lang_label.setText(HelloApplication.languageManager.get("ui.configpage.lang_label.name"));
    }
    public void setMem(Number mem){
        HelloApplication.configReader.configModel.max_memory = mem.intValue();
        HelloApplication.configReader.write();
    }
    public void refreshType(){

    }
    private static String getKey(Map<String,String> map,String value){
        String key="";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(value.equals(entry.getValue())){
                key=entry.getKey();
            }
        }
        return key;
    }
}
