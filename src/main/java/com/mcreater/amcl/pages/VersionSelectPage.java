package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
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
    VBox dot_minecraft_dir;
    Label title;
    JFXComboBox<String> dirs;
    JFXButton add_dir;
    JFXListView<HBox> version_list;
    HBox buttons;
    VBox versionlist;
    Vector<String> r;
    String selected_version_name;
    Label select_version;
    String last;
    boolean checked;
    Vector<String> result;
    Logger logger = LogManager.getLogger(VersionSelectPage.class);
    boolean first_load = true;
    String last_dir;
    public VersionSelectPage(double width,double height){
        super(width, height);
        l = Application.MAINPAGE;
        set();

        double t_size = Application.barSize;

        checked = false;

        last = Application.configReader.configModel.selected_version_index;
        last_dir = Application.configReader.configModel.selected_minecraft_dir_index;

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

        if (Application.configReader.configModel.selected_minecraft_dir.contains(Application.configReader.configModel.selected_minecraft_dir_index)) {
            dirs.getSelectionModel().select(Application.configReader.configModel.selected_minecraft_dir.indexOf(Application.configReader.configModel.selected_minecraft_dir_index));
        }
        else {
            Application.configReader.configModel.selected_minecraft_dir_index = "";
            Application.configReader.configModel.selected_version_index = "";
            select_version.setText("");
            try {
                Application.configReader.write();
            }
            catch (Exception ignored){
            }
        }

        dirs.setOnAction(event -> {
            checked = false;
            selected_version_name = "";
            update_version_name();
            load_list();
        });

        add_dir = new JFXButton();
        add_dir.setFont(Fonts.t_f);
        add_dir.setDefaultButton(true);
        add_dir.setOnAction(event -> {
            DirectoryChooser directoryChooser=new DirectoryChooser();
            Runnable setDialog = () -> FastInfomation.create(Application.languageManager.get("ui.versionselectpage.error_dir.title"), Application.languageManager.get("ui.versionselectpage.error_dir.Headercontent"),"");
            File file = directoryChooser.showDialog(Application.stage);
            if (file == null){
                setDialog.run();
            }
            else {
                String path = file.getPath();
                Vector<String> result = getMinecraftVersion.get(path);
                if (result == null) {
                    setDialog.run();
                } else {
                    Application.configReader.configModel.selected_minecraft_dir.add(path);
                    Application.configReader.write();
                    dirs.getSelectionModel().select(path);
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

        JFXSmoothScroll.smoothScrollingListView(version_list, 0.5);
        JFXSmoothScroll.smoothHScrollingListView(version_list, 0.5);

        this.add(dot_minecraft_dir, 0, 0, 1, 1);
        this.add(versionlist,1,0 ,1,1);
    }
    public void load_minecraft_dir(){
        version_list.getItems().clear();
        dirs.getItems().clear();
        for (String p : Application.configReader.configModel.selected_minecraft_dir) {
            dirs.getItems().add(p);
        }
    }
    public void load_list(){
        Runnable load = () -> {
            Platform.runLater(MainPage.l::show);
            setTypeAll(true);
            if (!Objects.equals(dirs.getValue(), null)) {
                Application.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
                Application.configReader.write();

                result = getMinecraftVersion.get(Application.configReader.configModel.selected_minecraft_dir_index);
                Platform.runLater(version_list.getItems()::clear);
                if (result != null) {
                    int dg = result.size();
                    int ld = 0;
                    for (String s : result) {
                        logger.info(String.format("loading version %s", s));
                        String f = "error";
                        if (!new File(LinkPath.link(LinkPath.link(LinkPath.link(Application.configReader.configModel.selected_minecraft_dir_index, "versions"), s), s + ".json")).exists()) {
                            logger.warn(String.format("Failed to load version name %s !", s));
                            continue;
                        }
                        try {
                            f = versionTypeGetter.get(Application.configReader.configModel.selected_minecraft_dir_index, s);
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
                        });
                        ld += 1;
                        MainPage.l.setV(0, ld * 100 / dg, String.format(Application.languageManager.get("ui.versionListLoad._01"), s));
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
        Application.configReader.configModel.selected_version_index = selected_version_name;
        Application.configReader.write();
    }
    public void refresh(){
        load_list();
    }
    public void refreshType(){
        last = Application.configReader.configModel.selected_version_index;
        last_dir = Application.configReader.configModel.selected_minecraft_dir_index;
    }

    public void onExitPage() {
//        if (!checked){
//            if (!Objects.equals(dirs.getValue(), last_dir)) {
//                dirs.getSelectionModel().select(last_dir);
//            }
//            selected_version_name = last;
//            update_version_name();
//            Application.configReader.configModel.selected_version_index = last;
//            Application.configReader.configModel.selected_minecraft_dir_index = last_dir;
//            Application.configReader.write();
//        }
//        else {
//            Application.configReader.configModel.selected_version_index = selected_version_name;
//            Application.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
//            Application.configReader.write();
//        }
        Application.configReader.configModel.selected_version_index = selected_version_name;
        Application.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
        Application.configReader.write();
    }

    public void refreshLanguage(){
        name = Application.languageManager.get("ui.versionselectpage.name");
        title.setText(Application.languageManager.get("ui.versionselectpage.title.name"));
        add_dir.setText(Application.languageManager.get("ui.versionselectpage.add_dir.name"));
    }
}
