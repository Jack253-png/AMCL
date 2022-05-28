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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.Objects;
import java.util.Vector;
import static com.mcreater.amcl.pages.MainPage.logger;

public class VersionSelectPage extends AbstractAnimationPage {
    VBox dot_minecraft_dir;
    Label title;
    JFXComboBox<String> dirs;
    JFXButton add_dir;
    JFXButton quit;
    JFXListView<Node> version_list;
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
    public VersionSelectPage(double widthl,double heightl){
        l = HelloApplication.MAINPAGE;
        set();

        checked = false;
        width = widthl;
        height = heightl;

        last = HelloApplication.configReader.configModel.selected_version_index;

        version_list = new JFXListView<>();
        version_list.setMaxSize(width / 2,height / 100 * 85);
        version_list.setMinSize(width / 2,height / 100 * 85);

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
            HelloApplication.configReader.configModel.selected_version_index = "";
            try {
                HelloApplication.configReader.write();
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

        quit = new JFXButton();
        quit.setFont(Fonts.t_f);
        quit.setDefaultButton(true);
        quit.setOnAction(event -> {
            if (!checked){
                HelloApplication.configReader.configModel.selected_version_index = last;
                HelloApplication.configReader.write();
            }
            if (getCanMovePage()) {
                HelloApplication.configReader.configModel.selected_version_index = selected_version_name;
                HelloApplication.configReader.write();
                HelloApplication.setPage(HelloApplication.MAINPAGE);
            }
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
                return;
            }
            String path = file.getPath();
            Vector<String> result = getMinecraftVersion.get(path);

            if (result == null){
                setDialog.run();
            }
            else{
                HelloApplication.configReader.configModel.selected_minecraft_dir.add(path);
                HelloApplication.configReader.write();
                dirs.getSelectionModel().select("");
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
        for (String p : HelloApplication.configReader.configModel.selected_minecraft_dir) {
            dirs.getItems().add(p);
        }
        Vector<Integer> reseved_index_list = new Vector<>();
        for (int i = 0;i < dirs.getItems().size();i++){
            for (int j = 1;j < dirs.getItems().size();j++){
                if (i != j) {
                    if (dirs.getItems().get(i).equals(dirs.getItems().get(j))) {
                        reseved_index_list.add(j);
                    }
                }
            }
        }
        Vector<String> reseved_string_list = new Vector<>();
        for (int n : reseved_index_list){
            reseved_string_list.add(dirs.getItems().get(n));
        }
        for (String pt : reseved_string_list){
            dirs.getItems().remove(pt);
        }
    }
    public void load_list(){
        if (!Objects.equals(dirs.getValue(), null)) {

            HelloApplication.configReader.configModel.selected_minecraft_dir_index = dirs.getValue();
            HelloApplication.configReader.write();

            Vector<String> result = getMinecraftVersion.get(HelloApplication.configReader.configModel.selected_minecraft_dir_index);
            version_list.getItems().clear();
            if (result != null) {
                for (String s : result) {
                    String f = "error";
                    if (!new File(LinkPath.link(LinkPath.link(LinkPath.link(HelloApplication.configReader.configModel.selected_minecraft_dir_index, "versions"), s), s + ".json")).exists()){
                        logger.warn("Failed to load version name " + s + "!");
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
                    if (Objects.equals(f, "original")) {
                        versionType.setImage(original);
                    } else if (Objects.equals(f, "forge") || Objects.equals(f, "forge-optifine")) {
                        versionType.setImage(forge);
                    } else if (Objects.equals(f, "fabric")) {
                        versionType.setImage(fabric);
                    } else if (Objects.equals(f, "liteloader")) {
                        versionType.setImage(liteloader);
                    } else if (Objects.equals(f, "optifine")) {
                        versionType.setImage(optifine);
                    }
                    HBox butt = new HBox();
                    butt.setAlignment(Pos.CENTER);
                    JFXButton v = new JFXButton(s);
                    v.setDefaultButton(true);
                    v.setFont(Fonts.t_f);
                    v.setOnAction(event -> {
                        checked = true;
                        selected_version_name = v.getText();
                        update_version_name();
                    });
                    v.setButtonType(JFXButton.ButtonType.RAISED);
                    butt.getChildren().add(v);
                    version.getChildren().addAll(versionType, butt);
                    version_list.getItems().add(version);
                }
            }
        }
    }
    public void update_version_name(){
        select_version.setText(selected_version_name);
    }
    public void refresh(){

    }
    public void refreshLanguage(){
        name = HelloApplication.languageManager.get("ui.versionselectpage.name");
        title.setText(HelloApplication.languageManager.get("ui.versionselectpage.title.name"));
        quit.setText(HelloApplication.languageManager.get("ui.versionselectpage.ok.name"));
        add_dir.setText(HelloApplication.languageManager.get("ui.versionselectpage.add_dir.name"));
    }
}
