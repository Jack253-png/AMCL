package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.BooleanItem;
import com.mcreater.amcl.controls.items.IntItem;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.MuiltButtonListItem;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.JavaInfoGetter;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class ConfigPage extends AbstractAnimationPage {
    VBox mainBox;
    Label title;

    VBox configs_box;
    JFXButton java_add;
    JFXButton java_get;
    Map<String, String> langs;
    VBox menu;
    JFXButton setting;
    SettingPage last;
    SettingPage p1;
    SettingPage p2;
    JFXButton setted;
    BooleanItem item;
    IntItem item2;
    ListItem<Label> item3;
    MuiltButtonListItem<Label> item4;
    BooleanItem item5;
    IntItem item6;
    public ConfigPage(int width, int height){
        super(width, height);
        l = Launcher.MAINPAGE;
        set();
        this.setAlignment(Pos.TOP_CENTER);

        double t_size = Launcher.barSize;

        langs = new TreeMap<>();
        langs.put("简体中文", "CHINESE");
        langs.put("English(US)", "ENGLISH");

        title = new Label();
        title.setFont(Fonts.b_f);

        item = new BooleanItem("", this.width / 4 * 3);
        item.cont.setSelected(Launcher.configReader.configModel.change_game_dir);
        item.cont.selectedProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.change_game_dir = newValue);

        item2 = new IntItem("", this.width / 4 * 3);
        item2.cont.setMax(4096);
        item2.cont.setMin(256);
        item2.cont.setValue(Launcher.configReader.configModel.max_memory);
        item2.cont.setOrientation(Orientation.HORIZONTAL);
        item2.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.max_memory = newValue.intValue());

        java_add = new JFXButton();
        java_add.setDefaultButton(true);
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Launcher.languageManager.get("ui.configpage.java_choose.title"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.configpage.java_choose.filename.description"), "java.exe"));
            File choosed_path = fileChooser.showOpenDialog(Launcher.stage);
            if (choosed_path != null) {
                if (!Launcher.configReader.configModel.selected_java.contains(choosed_path.getPath())) {
                    Launcher.configReader.configModel.selected_java.add(choosed_path.getPath());
                    load_java_list();
                }
            }
        });

        java_get = new JFXButton();
        java_get.setDefaultButton(true);
        java_get.setFont(Fonts.t_f);
        java_get.setOnAction(event -> new Thread(() -> {
            java_get.setDisable(true);
            if (Launcher.configReader.configModel.selected_java.contains(Launcher.configReader.configModel.selected_java_index) && new File(Launcher.configReader.configModel.selected_java_index).exists()) {
                Vector<String> v;
                try {
                    v = JavaInfoGetter.get(new File(Launcher.configReader.configModel.selected_java_index));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.configpage.java_info.title"), String.format(Launcher.languageManager.get("ui.configpage.java_info.Headercontent"), v.get(0), v.get(1), v.get(2), v.get(3)), ""));
            }
            else{
                Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.configpage.select_java.title"), Launcher.languageManager.get("ui.configpage.select_java.Headercontent"), ""));
            }
            java_get.setDisable(false);
        }).start());

        item4 = new MuiltButtonListItem<>("", this.width / 4 * 3);
        item4.addButtons(java_get, java_add);
        item4.cont.setOnAction(event -> {
            if (item4.cont.getValue() == null && item4.cont.getItems().size() > 0) {
                item4.cont.getSelectionModel().select(0);
            }
            Launcher.configReader.configModel.selected_java_index = item4.cont.getValue().getText();
            Launcher.configReader.write();
        });

        load_java_list();

        item3 = new ListItem<>("", this.width / 4 * 3);
        for (Map.Entry<String, String> entry : langs.entrySet()) {
            Label l = new Label(entry.getKey());
            l.setFont(Fonts.t_f);
            item3.cont.getItems().add(l);
        }
        item3.cont.getSelectionModel().select(getKey(Launcher.configReader.configModel.language));
        item3.cont.setOnAction(event -> {
            Launcher.configReader.configModel.language = langs.get(item3.cont.getValue().getText());
            Launcher.languageManager.setLanguage(LanguageManager.valueOf(Launcher.configReader.configModel.language));
            Launcher.setTitle();
        });

        item5 = new BooleanItem("", this.width / 4 * 3);
        item5.cont.selectedProperty().set(Launcher.configReader.configModel.fastDownload);
        item5.cont.setOnAction(event -> Launcher.configReader.configModel.fastDownload = item5.cont.isSelected());

        item6 = new IntItem("", this.width / 4 * 3);
        item6.cont.setMax(8192);
        item6.cont.setMin(512);
        item6.cont.setValue(Launcher.configReader.configModel.downloadChunkSize);
        item6.cont.setOrientation(Orientation.HORIZONTAL);
        item6.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.downloadChunkSize = newValue.intValue());

        SetSize.setHeight(item, 30);
        SetSize.setHeight(item2, 30);
        SetSize.setHeight(item2, 30);
        SetSize.setHeight(item3, 30);
        SetSize.setHeight(item4, 30);
        SetSize.setHeight(item5, 30);
        SetSize.setHeight(item6, 30);

        configs_box = new VBox();
        configs_box.setSpacing(10);
        configs_box.getChildren().addAll(item, item2, item3, item4, item5, item6);
        configs_box.setId("config-box");

        java_get.setButtonType(JFXButton.ButtonType.RAISED);
        java_add.setButtonType(JFXButton.ButtonType.RAISED);

        mainBox = new VBox();

        p1 = new SettingPage(this.width / 4 * 3, this.height - t_size, configs_box);

        last = null;

        setting = new JFXButton();
        setting.setFont(Fonts.s_f);
        setting.setOnAction(event -> {
            setP1(p1);
            setType(setting);
        });
        SetSize.setWidth(setting, this.width / 4);

        menu = new VBox();
        menu.setId("config-menu");
        menu.getChildren().addAll(setting);
        SetSize.set(menu, this.width / 4,this.height - t_size);

        setP1(p1);
        setType(setting);
    }
    public void setType(JFXButton b){
        setted = b;
        for (Node bs : menu.getChildren()){
            bs.setDisable(bs == b);
        }
    }
    public void setP1(SettingPage p){
        if (p.CanMovePage() && last != p) {
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
        }
    }
    public void load_java_list(){
        item4.cont.getItems().clear();
        for (String s : Launcher.configReader.configModel.selected_java) {
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            item4.cont.getItems().add(l);
        }
        if (Launcher.configReader.configModel.selected_java.contains(Launcher.configReader.configModel.selected_java_index) || new File(Launcher.configReader.configModel.selected_java_index).exists()){
            item4.cont.getSelectionModel().select(Launcher.configReader.configModel.selected_java.indexOf(Launcher.configReader.configModel.selected_java_index));
        }
        else{
            item4.cont.getSelectionModel().clearSelection();
        }
    }
    public void refresh(){
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
        setType(setted);
    }
    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.configpage.name");
        title.setText(Launcher.languageManager.get("ui.configpage.title.name"));

        item.name.setText(Launcher.languageManager.get("ui.configpage.change_label.name"));
        item2.name.setText(Launcher.languageManager.get("ui.configpage.mem_label.name"));
        item3.name.setText(Launcher.languageManager.get("ui.configpage.lang_label.name"));
        item4.name.setText(Launcher.languageManager.get("ui.configpage.java_label.name"));
        item5.name.setText(Launcher.languageManager.get("ui.configpage.item5.name"));
        item6.name.setText(Launcher.languageManager.get("ui.configpage.item6.name"));

        java_get.setText(Launcher.languageManager.get("ui.configpage.java_get.name"));
        java_add.setText(Launcher.languageManager.get("ui.configpage.java_add.name"));
        setting.setText(Launcher.languageManager.get("ui.configpage.menu._01"));
    }

    public void refreshType(){

    }

    public void onExitPage() {

    }

    private int getKey(String value){
        for (Label l : item3.cont.getItems()){
            String key = "";
            for (Map.Entry<String, String> entry : langs.entrySet()){
                if (Objects.equals(entry.getValue(), value)){
                    key = entry.getKey();
                }
            }
            if (Objects.equals(l.getText(), key)){
                return item3.cont.getItems().indexOf(l);
            }
        }
        return -1;
    }
}
