package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.controls.VersionItem;
import com.mcreater.amcl.game.GetMinecraftVersion;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.Vector;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class VersionSelectPage extends AbstractAnimationPage {
    public VBox dot_minecraft_dir;
    public Label title;
    public JFXComboBox<Label> dirs;
    public JFXButton add_dir;
    public SmoothableListView<VersionItem> version_list;
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

        version_list = new SmoothableListView<>(width / 4 * 3, height);
        version_list.setOnAction(() -> {
            try {
                checked = true;
                selected_version_name = version_list.selectedItem.getVersion();
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

        add_dir = new JFXButton();
        add_dir.setFont(Fonts.t_f);
        add_dir.setDefaultButton(true);
        add_dir.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            Runnable setDialog = () -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.versionselectpage.error_dir.title"), Launcher.languageManager.get("ui.versionselectpage.error_dir.Headercontent"),"");
            File file = directoryChooser.showDialog(Launcher.stage);
            if (file == null){
                setDialog.run();
            }
            else {
                String path = file.getPath();
                new File(LinkPath.link(path, "versions")).mkdirs();
                Vector<String> result = GetMinecraftVersion.get(path);
                if (result == null) {
                    setDialog.run();
                } else {
                    Launcher.configReader.configModel.selected_minecraft_dir.add(path);
                    Launcher.configReader.write();
                    load_minecraft_dir();
                    dirs.getSelectionModel().select(findLabelFromName(path));
                    r = result;
                }
            }
        });

        dot_minecraft_dir = new VBox();
        FXUtils.ControlSize.set(dot_minecraft_dir, this.width / 4,this.height);
        dot_minecraft_dir.setAlignment(Pos.TOP_CENTER);

        buttons = new HBox();
        buttons.getChildren().addAll(add_dir);
        buttons.setStyle("-fx-background-color: rgba(255,255,255,0.0);");
        buttons.setAlignment(Pos.TOP_CENTER);

        versionlist = new VBox();
        versionlist.setAlignment(Pos.TOP_CENTER);

        FXUtils.ControlSize.set(version_list.page, this.width / 4 * 3,this.height - t_size);
        FXUtils.ControlSize.setWidth(version_list, this.width / 4 * 3 - 25);
        versionlist.getChildren().add(version_list.page);
        versionlist.setId("game-menu");

        dot_minecraft_dir.getChildren().addAll(title,dirs,new MainPage.Spacer(),select_version,new MainPage.Spacer(),buttons);

        add_dir.setButtonType(JFXButton.ButtonType.RAISED);

        ThemeManager.applyNode(dirs);

        AdvancedScrollPane p = new AdvancedScrollPane(width / 4, height, dot_minecraft_dir);
        ThemeManager.loadButtonAnimates(title, dirs, select_version, buttons);

        this.add(p, 0, 0, 1, 1);
        this.add(versionlist,1,0 ,1,1);
        nodes.add(null);
        bindedPageproperty().get().addAll(J8Utils.createList(
                ADDMODSPAGE,
                CONFIGPAGE,
                DOWNLOADADDONSELECTPAGE,
                DOWNLOADMCPAGE,
                MODDOWNLOADPAGE,
                USERSELECTPAGE,
                VERSIONINFOPAGE,
                VERSIONSELECTPAGE
        ));

        onExitPage();
    }
    public int findLabelFromName(String name){
        for (int i = 0;i < dirs.getItems().size();i++){
            if (Objects.equals(dirs.getItems().get(i).getText(), name)){
                return i;
            }
        }
        return -1;
    }
    public void load_minecraft_dir(){
        version_list.clear();
        dirs.getItems().clear();
        for (String p : Launcher.configReader.configModel.selected_minecraft_dir) {
            Label l = new Label(p);
            l.setFont(Fonts.t_f);
            dirs.getItems().add(l);
        }
    }
    public void load_list(){
        Runnable load = () -> {
            Platform.runLater(MainPage.versionLoadDialog::show);
            dirs.setDisable(true);
            add_dir.setDisable(true);
            version_list.setDisable(true);
            if (!Objects.equals(dirs.getValue(), null)) {
                Launcher.configReader.configModel.selected_minecraft_dir_index = dirs.getValue().getText();
                Launcher.configReader.write();

                result = GetMinecraftVersion.get(Launcher.configReader.configModel.selected_minecraft_dir_index);
                Platform.runLater(version_list::clear);
                if (result != null) {
                    int dg = result.size();
                    int ld = 0;
                    for (String s : result) {
                        logger.info(String.format("loading version %s", s));
                        VersionTypeGetter.VersionType f = null;
                        if (!new File(LinkPath.link(LinkPath.link(LinkPath.link(Launcher.configReader.configModel.selected_minecraft_dir_index, "versions"), s), s + ".json")).exists()) {
                            logger.warn(String.format("Failed to load version name %s !", s));
                            continue;
                        }
                        try {
                            f = VersionTypeGetter.get(Launcher.configReader.configModel.selected_minecraft_dir_index, s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        VersionTypeGetter.VersionType finalF = f;
                        Platform.runLater(() -> {
                            VersionItem i = new VersionItem(s, finalF);
                            version_list.addItem(i);
                            ThemeManager.loadButtonAnimates(i);
                        });
                        ld += 1;
                        MainPage.versionLoadDialog.setV(0, ld * 100 / dg, String.format(Launcher.languageManager.get("ui.versionListLoad._01"), s));
                    }
                }
            }
            dirs.setDisable(false);
            add_dir.setDisable(false);
            version_list.setDisable(false);
            MainPage.versionLoadDialog.setV(0, 100, "");
            Platform.runLater(MainPage.versionLoadDialog::close);
        };
        new Thread(load).start();
    }
    public void update_version_name(){
        Platform.runLater(() -> select_version.setText(selected_version_name));
        Launcher.configReader.configModel.selected_version_index = selected_version_name;
        Launcher.configReader.write();
    }
    public void refresh(){
        update_version_name();
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
