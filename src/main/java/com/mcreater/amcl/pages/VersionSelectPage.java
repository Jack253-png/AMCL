package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.config.ConfigWriter;
import com.mcreater.amcl.game.getMinecraftVersion;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class VersionSelectPage extends AbstractAnimationPage{
    VBox dot_minecraft_dir;
    Label title;
    JFXComboBox<String> dirs;
    JFXButton add_dir;
    JFXButton quit;
    ConfigWriter configWriter;
    JFXListView<JFXButton> version_list;
    HBox buttons;
    VBox versionlist;
    Vector<String> r;
    String selected_version_name;
    Label select_version;
    double width;
    double height;
    String last;
    boolean checked;
    public VersionSelectPage(double widthl,double heightl,Background bg){
        this.setBackground(bg);
        set(this);
        in.play();
        set(this);

        checked = false;
        width = widthl;
        height = heightl;

        try {
            configWriter = new ConfigWriter(new File(HelloApplication.config_base_path + "config.json"));
        } catch (IOException ignored) {
            throw new IllegalStateException("Null Config File");
        }

        last = configWriter.configModel.selected_version_index;

        version_list = new JFXListView<>();
        version_list.setMaxSize(width / 2,height / 100 * 95);
        version_list.setMinSize(width / 2,height / 100 * 95);

        title = new Label("Minecraft Dir");
        title.setFont(Fonts.s_f);

        dirs = new JFXComboBox<>();
        load_minecraft_dir();

        select_version = new Label();
        select_version.setFont(Fonts.s_f);
        update_version_name();

        if (configWriter.configModel.selected_minecraft_dir.contains(configWriter.configModel.selected_minecraft_dir_index)) {
            dirs.getSelectionModel().select(configWriter.configModel.selected_minecraft_dir.indexOf(configWriter.configModel.selected_minecraft_dir_index));
        }
        else {
            configWriter.configModel.selected_version_index = "";
            try {
                configWriter.write();
            }
            catch (Exception ignored){
            }
        }

        load_list();

        dirs.setOnAction(event -> {
            selected_version_name = "";
            update_version_name();
            load_list();
        });

        quit = new JFXButton("Ok");
        quit.setFont(Fonts.t_f);
        quit.setDefaultButton(true);
        quit.setOnAction(event -> {
            if (!checked){
                configWriter.configModel.selected_version_index = last;
                try {
                    configWriter.write();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            HelloApplication.setPage(new MainPage(width, height, bg));
        });

        add_dir = new JFXButton("Add");
        add_dir.setFont(Fonts.t_f);
        add_dir.setDefaultButton(true);
        add_dir.setOnAction(event -> {
            DirectoryChooser directoryChooser=new DirectoryChooser();
            File file = directoryChooser.showDialog(HelloApplication.stage);
            if (file == null){
                FastInfomation.create("Null Minecraft Dir","Unsupported Minecraft Dir","");
                return;
            }
            String path = file.getPath();
            Vector<String> result = getMinecraftVersion.get(path);

            if (result == null){
                FastInfomation.create("Null Minecraft Dir","Unsupported Minecraft Dir","");
            }
            else{
                configWriter.configModel.selected_minecraft_dir.add(path);
                try {
                    configWriter.write();
                } catch (IOException ignored) {
                }
                load_minecraft_dir();
                r = result;
            }
        });

        dot_minecraft_dir = new VBox();
        dot_minecraft_dir.setMaxSize(width / 4,height);
        dot_minecraft_dir.setMinSize(width / 4,height);
        dot_minecraft_dir.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        dot_minecraft_dir.setAlignment(Pos.TOP_CENTER);

        buttons = new HBox();
        buttons.getChildren().addAll(add_dir,quit);
        buttons.setStyle("-fx-background-color: rgba(255,255,255,0.0);");
        buttons.setAlignment(Pos.TOP_CENTER);

        versionlist = new VBox();
        versionlist.setMaxSize(width / 4 * 3,height);
        versionlist.setMinSize(width / 4 * 3,height);
        versionlist.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        versionlist.setAlignment(Pos.CENTER);

        versionlist.getChildren().addAll(version_list);

        dot_minecraft_dir.getChildren().addAll(title,dirs,new MainPage.Spacer(),select_version,new MainPage.Spacer(),buttons);

        this.add(dot_minecraft_dir, 0, 0, 1, 1);
        this.add(versionlist,1,0,1,1);

    }
    public void load_minecraft_dir(){
        version_list.getItems().clear();
        for (String p : configWriter.configModel.selected_minecraft_dir) {
            dirs.getItems().add(p);
        }
    }
    public void load_list(){
        if (!Objects.equals(dirs.getValue(), null)){

            configWriter.configModel.selected_minecraft_dir_index = dirs.getValue();
            try {
                configWriter.write();
            } catch (IOException ignored) {
            }

            Vector<String> result = getMinecraftVersion.get(configWriter.configModel.selected_minecraft_dir_index);
            version_list.getItems().clear();
            for (String s : result){
                JFXButton v = new JFXButton(s);
                v.setDefaultButton(true);
                v.setFont(Fonts.t_f);
                v.setOnAction(event -> {
                    checked = true;
                    selected_version_name = v.getText();
                    update_version_name();
                });
                version_list.getItems().add(v);
            }
        }
    }
    public void update_version_name(){
        select_version.setText(selected_version_name);
        configWriter.configModel.selected_version_index = selected_version_name;
        try {
            configWriter.write();
        }
        catch (IOException ignored){
        }
    }
}
