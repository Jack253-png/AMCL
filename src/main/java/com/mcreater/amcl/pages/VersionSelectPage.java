package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.mcreater.amcl.HelloApplication;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.LinkPath;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

public class VersionSelectPage extends AbstractAnimationPage {
    VBox dot_minecraft_dir;
    Label title;
    JFXComboBox<String> dirs;
    JFXButton add_dir;
    JFXButton quit;
    JFXListView<HBox> version_list;
    HBox buttons;
    VBox versionlist;
    Vector<String> r;
    String selected_version_name;
    Label select_version;
    double width;
    double height;
    String last;
    boolean checked;

    Image original = new Image("assets/icons/original.png");
    Image forge = new Image("assets/icons/forge.png");
    Image fabric = new Image("assets/icons/fabric.png");
    Image liteloader = new Image("assets/icons/liteloader.png");
    Image optifine = new Image("assets/icons/optifine.png");
    Vector<String> result;
    Logger logger = LogManager.getLogger(VersionSelectPage.class);
    boolean first_load = true;
    String last_dir;
    public VersionSelectPage(double widthl,double heightl){
        l = HelloApplication.MAINPAGE;
        set();

        checked = false;
        width = widthl;
        height = heightl;

        last = HelloApplication.configReader.configModel.selected_version_index;
        last_dir = HelloApplication.configReader.configModel.selected_minecraft_dir_index;

        version_list = new JFXListView<>();
        version_list.setShowTooltip(true);
        version_list.setMaxSize(width / 2,height / 100 * 85);
        version_list.setMinSize(width / 2,height / 100 * 85);
        version_list.getSelectionModel().getSelectedItems().addListener((ListChangeListener<HBox>) c -> {
            checked = true;
            selected_version_name = ((Label) ((HBox) c.getList().get(0).getChildren().get(1)).getChildren().get(0)).getText();
            selected_version_name = selected_version_name.substring(1, selected_version_name.length());
            update_version_name();
        });

        title = new Label();
        title.setFont(Fonts.s_f);

        dirs = new JFXComboBox<>();
        load_minecraft_dir();

        select_version = new Label();
        select_version.setFont(Fonts.s_f);
        select_version.setText(last);

        if (HelloApplication.configReader.configModel.selected_minecraft_dir.contains(HelloApplication.configReader.configModel.selected_minecraft_dir_index)) {
            dirs.getSelectionModel().select(HelloApplication.configReader.configModel.selected_minecraft_dir.indexOf(HelloApplication.configReader.configModel.selected_minecraft_dir_index));
        }
        else {
            HelloApplication.configReader.configModel.selected_minecraft_dir_index = "";
            HelloApplication.configReader.configModel.selected_version_index = "";
            select_version.setText("");
            try {
                HelloApplication.configReader.write();
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

        quit = new JFXButton();
        quit.setFont(Fonts.t_f);
        quit.setDefaultButton(true);
        quit.setOnAction(event -> {
            if (!checked){
                HelloApplication.configReader.configModel.selected_version_index = last;
                HelloApplication.configReader.configModel.selected_minecraft_dir_index = last_dir;
                HelloApplication.configReader.write();
            }
            else {
                HelloApplication.configReader.configModel.selected_version_index = selected_version_name;
                HelloApplication.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
                HelloApplication.configReader.write();
            }
            HelloApplication.setPage(HelloApplication.MAINPAGE, this);
        });

        add_dir = new JFXButton();
        add_dir.setFont(Fonts.t_f);
        add_dir.setDefaultButton(true);
        add_dir.setOnAction(event -> {
            DirectoryChooser directoryChooser=new DirectoryChooser();
            Runnable setDialog = () -> FastInfomation.create(HelloApplication.languageManager.get("ui.versionselectpage.error_dir.title"),HelloApplication.languageManager.get("ui.versionselectpage.error_dir.Headercontent"),"");
            File file = directoryChooser.showDialog(HelloApplication.stage);
            if (file == null){
                setDialog.run();
            }
            else {
                String path = file.getPath();
                Vector<String> result = getMinecraftVersion.get(path);
                if (result == null) {
                    setDialog.run();
                } else {
                    HelloApplication.configReader.configModel.selected_minecraft_dir.add(path);
                    System.out.println(HelloApplication.configReader.configModel.selected_minecraft_dir);
                    HelloApplication.configReader.write();
                    dirs.getSelectionModel().select(path);
                    load_minecraft_dir();
                    r = result;
                }
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
        versionlist.setAlignment(Pos.TOP_CENTER);

        versionlist.getChildren().addAll(new MainPage.Spacer(), version_list);

        dot_minecraft_dir.getChildren().addAll(title,dirs,new MainPage.Spacer(),select_version,new MainPage.Spacer(),buttons);

        quit.setButtonType(JFXButton.ButtonType.RAISED);
        add_dir.setButtonType(JFXButton.ButtonType.RAISED);

        this.add(dot_minecraft_dir, 0, 1, 1, 1);
        this.add(versionlist,1,1 ,1,1);

    }
    public void load_minecraft_dir(){
        version_list.getItems().clear();
        dirs.getItems().clear();
        for (String p : HelloApplication.configReader.configModel.selected_minecraft_dir) {
            dirs.getItems().add(p);
        }
    }
    public void load_list(){
        Runnable load = () -> {
//            MainPage.l.show();
            setTypeAll(true);
            if (!Objects.equals(dirs.getValue(), null)) {
                HelloApplication.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
                HelloApplication.configReader.write();

                result = getMinecraftVersion.get(HelloApplication.configReader.configModel.selected_minecraft_dir_index);
                version_list.getItems().clear();
                if (result != null) {
                    int dg = result.size();
                    int ld = 0;
                    for (String s : result) {
                        try {
                            Thread.sleep(75);
                        } catch (InterruptedException ignored) {
                        }
                        logger.info(String.format("loading version %s", s));
                        String f = "error";
                        if (!new File(LinkPath.link(LinkPath.link(LinkPath.link(HelloApplication.configReader.configModel.selected_minecraft_dir_index, "versions"), s), s + ".json")).exists()) {
                            logger.warn(String.format("Failed to load version name %s !", s));
                            continue;
                        }
                        try {
                            f = versionTypeGetter.get(HelloApplication.configReader.configModel.selected_minecraft_dir_index, s);
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
                            Label v = new Label(" "+s);
                            v.setFont(Fonts.t_f);
                            butt.getChildren().add(v);
                            version.getChildren().addAll(versionType, butt);
                            version_list.getItems().add(version);
                        });
                        ld += 1;
                        MainPage.l.setV(0, ld * 100 / dg, String.format(HelloApplication.languageManager.get("ui.versionListLoad._01"), s));
                    }
                }
            }
            setTypeAll(false);
            MainPage.l.setV(0, 100, "");
            MainPage.l.close();
        };
        load.run();
//        new Thread(load).start();
    }
    public void update_version_name(){
        select_version.setText(selected_version_name);
        HelloApplication.configReader.configModel.selected_version_index = selected_version_name;
        HelloApplication.configReader.write();
    }
    public void refresh(){
        load_list();
//        if (first_load) {
//            load_list();
//            first_load = false;
//        }
//        else {
//            Vector<String> rde = getMinecraftVersion.get(HelloApplication.configReader.configModel.selected_minecraft_dir_index);
//            assert rde != null;
//            if (result.size() == rde.size()) {
//                for (int i = 0; i < rde.size(); i++) {
//                    if (!Objects.equals(result.get(i), rde.get(i))) {
//                        load_list();
//                        break;
//                    }
//                }
//            } else {
//                load_list();
//            }
//        }
    }
    public void refreshType(){
    }
    public void refreshLanguage(){
        name = HelloApplication.languageManager.get("ui.versionselectpage.name");
        title.setText(HelloApplication.languageManager.get("ui.versionselectpage.title.name"));
        quit.setText(HelloApplication.languageManager.get("ui.versionselectpage.ok.name"));
        add_dir.setText(HelloApplication.languageManager.get("ui.versionselectpage.add_dir.name"));
    }
}
