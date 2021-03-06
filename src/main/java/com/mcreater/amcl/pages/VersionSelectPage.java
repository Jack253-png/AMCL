package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.util.Images.*;

public class VersionSelectPage extends AbstractAnimationPage {
    public VBox dot_minecraft_dir;
    public Label title;
    public JFXComboBox<Label> dirs;
    public JFXButton add_dir;
    public JFXListView<HBox> version_list;
    public HBox buttons;
    public VBox versionlist;
    public Vector<String> r;
    public String selected_version_name;
    public Label select_version;
    public String last;
    boolean checked;
    public Vector<String> result;
    public Logger logger = LogManager.getLogger(VersionSelectPage.class);
    public String last_dir;
    public VersionSelectPage(double width,double height){
        super(width, height);
        l = Launcher.MAINPAGE;

        double t_size = Launcher.barSize;

        checked = false;

        last = Launcher.configReader.configModel.selected_version_index;
        last_dir = Launcher.configReader.configModel.selected_minecraft_dir_index;

        version_list = new JFXListView<>();
        version_list.getSelectionModel().getSelectedItems().addListener((ListChangeListener<HBox>) c -> {
            try {
                checked = true;
                selected_version_name = ((Label) ((HBox) c.getList().get(0).getChildren().get(1)).getChildren().get(0)).getText();
                update_version_name();
            }
            catch (IndexOutOfBoundsException e){
                checked = false;
                logger.warn("Failed to read select version!");
            }
        });

        title = new Label();
        title.setFont(Fonts.s_f);

        dirs = new JFXComboBox<>();
        load_minecraft_dir();

        select_version = new Label();
        select_version.setFont(Fonts.s_f);
        select_version.setText(last);

        if (Launcher.configReader.configModel.selected_minecraft_dir.contains(Launcher.configReader.configModel.selected_minecraft_dir_index)) {
            dirs.getSelectionModel().select(Launcher.configReader.configModel.selected_minecraft_dir.indexOf(Launcher.configReader.configModel.selected_minecraft_dir_index));
        }
        else {
            Launcher.configReader.configModel.selected_minecraft_dir_index = "";
            Launcher.configReader.configModel.selected_version_index = "";
            select_version.setText("");
            try {
                Launcher.configReader.write();
            }
            catch (Exception ignored){
            }
        }

        dirs.setOnAction(event -> {
            checked = true;
            selected_version_name = "";
            update_version_name();
            load_list();
        });
        dirs.setStyle("-fx-background-color: transparent");

        add_dir = new JFXButton();
        add_dir.setFont(Fonts.t_f);
        add_dir.setDefaultButton(true);
        add_dir.setOnAction(event -> {
            DirectoryChooser directoryChooser=new DirectoryChooser();
            Runnable setDialog = () -> FastInfomation.create(Launcher.languageManager.get("ui.versionselectpage.error_dir.title"), Launcher.languageManager.get("ui.versionselectpage.error_dir.Headercontent"),"");
            File file = directoryChooser.showDialog(Launcher.stage);
            if (file == null){
                setDialog.run();
            }
            else {
                String path = file.getPath();
                Vector<String> result = getMinecraftVersion.get(path);
                if (result == null) {
                    setDialog.run();
                } else {
                    Launcher.configReader.configModel.selected_minecraft_dir.add(path);
                    Launcher.configReader.write();
                    dirs.getSelectionModel().select(findLabelFromName(path));
                    load_minecraft_dir();
                    r = result;
                }
            }
        });

        dot_minecraft_dir = new VBox();
        SetSize.set(dot_minecraft_dir, this.width / 4,this.height);
        dot_minecraft_dir.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        dot_minecraft_dir.setAlignment(Pos.TOP_CENTER);

        buttons = new HBox();
        buttons.getChildren().addAll(add_dir);
        buttons.setStyle("-fx-background-color: rgba(255,255,255,0.0);");
        buttons.setAlignment(Pos.TOP_CENTER);

        versionlist = new VBox();
        version_list.setVerticalGap(0.0);
//        setSize.set(versionlist, this.width / 4 * 3,this.height);
        versionlist.setStyle("-fx-background-color: rgba(255,255,255,0.75);");
        versionlist.setAlignment(Pos.TOP_CENTER);

        SetSize.set(version_list, this.width / 4 * 3,this.height - t_size);
        versionlist.getChildren().add(version_list);

        dot_minecraft_dir.getChildren().addAll(title,dirs,new MainPage.Spacer(),select_version,new MainPage.Spacer(),buttons);

        add_dir.setButtonType(JFXButton.ButtonType.RAISED);

        this.add(dot_minecraft_dir, 0, 0, 1, 1);
        this.add(versionlist,1,0 ,1,1);
    }
    public Label findLabelFromName(String name){
        for (Label l : dirs.getItems()){
            if (Objects.equals(l.getText(), name)){
                return l;
            }
        }
        return null;
    }
    public void load_minecraft_dir(){
        version_list.getItems().clear();
        dirs.getItems().clear();
        for (String p : Launcher.configReader.configModel.selected_minecraft_dir) {
            Label l = new Label(p);
            l.setFont(Fonts.t_f);
            dirs.getItems().add(l);
        }
    }
    public void load_list(){
        Runnable load = () -> {
            Platform.runLater(MainPage.l::show);
            setTypeAll(true);
            if (!Objects.equals(dirs.getValue(), null)) {
                Launcher.configReader.configModel.selected_minecraft_dir_index = dirs.getValue().getText();
                Launcher.configReader.write();

                result = getMinecraftVersion.get(Launcher.configReader.configModel.selected_minecraft_dir_index);
                Platform.runLater(version_list.getItems()::clear);
                if (result != null) {
                    int dg = result.size();
                    int ld = 0;
                    for (String s : result) {
                        logger.info(String.format("loading version %s", s));
                        String f = "error";
                        if (!new File(LinkPath.link(LinkPath.link(LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "versions"), s), s + ".json")).exists()) {
                            logger.warn(String.format("Failed to load version name %s !", s));
                            continue;
                        }
                        try {
                            f = versionTypeGetter.get(Launcher.configReader.configModel.selected_minecraft_dir_index, s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        HBox version = new HBox();
                        ImageView versionType = new ImageView();
                        versionType.setFitWidth(40);
                        versionType.setFitHeight(40);
                        switch (f) {
                            case "original":
                                versionType.setImage(original);
                                break;
                            case "forge":
                            case "forge-optifine":
                                versionType.setImage(forge);
                                break;
                            case "fabric":
                                versionType.setImage(fabric);
                                break;
                            case "liteloader":
                                versionType.setImage(liteloader);
                                break;
                            case "optifine":
                                versionType.setImage(optifine);
                                break;
                        }
                        Platform.runLater(() -> {
                            HBox butt = new HBox();
                            butt.setAlignment(Pos.CENTER_LEFT);
                            Label v = new Label(s);
                            v.setFont(Fonts.t_f);
                            butt.getChildren().add(v);
                            version.setSpacing(15);
                            version.getChildren().addAll(versionType, butt);
                            version_list.getItems().add(version);
                            ThemeManager.loadButtonAnimates(versionType, v);
                        });
                        ld += 1;
                        MainPage.l.setV(0, ld * 100 / dg, String.format(Launcher.languageManager.get("ui.versionListLoad._01"), s));
                    }
                }
            }
            setTypeAll(false);
            MainPage.l.setV(0, 100, "");
            Platform.runLater(MainPage.l::close);
        };
//        load.run();
        new Thread(load).start();
    }
    public void update_version_name(){
        Platform.runLater(() -> select_version.setText(selected_version_name));
        Launcher.configReader.configModel.selected_version_index = selected_version_name;
        Launcher.configReader.write();
    }
    public void refresh(){
        load_list();
    }
    public void refreshType(){
        last = Launcher.configReader.configModel.selected_version_index;
        last_dir = Launcher.configReader.configModel.selected_minecraft_dir_index;
    }

    public void onExitPage() {
        if (!checked){
            if (dirs.getValue() != null) {
                if (!Objects.equals(dirs.getValue().getText(), last_dir)) {
                    dirs.getSelectionModel().select(findLabelFromName(last_dir));
                }
            }
            selected_version_name = last;
            update_version_name();
            Launcher.configReader.configModel.selected_version_index = last;
            Launcher.configReader.configModel.selected_minecraft_dir_index = last_dir;
            Launcher.configReader.write();
        }
        else {
            Launcher.configReader.configModel.selected_version_index = selected_version_name;
            Launcher.configReader.configModel.selected_minecraft_dir_index = dirs.getValue().getText();
            Launcher.configReader.write();
        }
    }

    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.versionselectpage.name");
        title.setText(Launcher.languageManager.get("ui.versionselectpage.title.name"));
        add_dir.setText(Launcher.languageManager.get("ui.versionselectpage.add_dir.name"));
    }
}
