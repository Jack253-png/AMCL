package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.game.getMinecraftVersion;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MainPage extends AbstractAnimationPage {
    public static Label title;
    public static Label launch;
    public static JFXButton choose_version;
    public static JFXButton version_settings;
    public static VBox GameMenu;
    public static HBox LaunchTitle;
    public static HBox SetTitle;
    public static Label set;
    public static JFXButton settings;
    public static JFXButton downloadMc;
    public static Label downloadTitle;
    public static JFXButton launchButton;
    public static VBox launchBox;
    public boolean is_vaild_minecraft_dir;
    public static ObservableList<Launch> game;
    public static Logger logger = LogManager.getLogger(MainPage.class);
    public static ProcessDialog launchDialog;
    public static ProcessDialog l;
    public static JFXButton users;
    public static JFXButton stopProcess;
    public static final AtomicBoolean clearingThread = new AtomicBoolean(false);
    public static void tryToRemoveLaunch(Launch launch){
        new Thread(() -> {
            while (true){
                if (!clearingThread.get()){
                    game.remove(launch);
                    break;
                }
            }
        }).start();
    }
    public void stopAllProcess(){
        clearingThread.set(true);
        game.forEach(Launch::stop_process);
        clearingThread.set(false);
    }
    public MainPage(double width,double height) {
        super(width, height);
        l = null;

        Runtime.getRuntime().addShutdownHook(new Thread(this::stopAllProcess));

        game = FXCollections.observableArrayList();

        launchButton = new JFXButton();
        launchButton.setId("launch-button");
        launchButton.setFont(Fonts.s_f);
        launchButton.setTextFill(Color.WHITE);
        launchButton.setOnAction(event -> {
            FileUtils.ChangeDir.saveNowDir();
            if (!Objects.equals(launchButton.getText(), Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"))) {
                Launcher.configReader.check_and_write();
                launchDialog = new ProcessDialog(3, Launcher.languageManager.get("ui.mainpage.launch._01")) {
                    public void setAll(int progress){
                        progresses.forEach(bar -> Platform.runLater(() -> bar.setProgress((double) progress / 100)));
                    }
                    public void setV(int index, int progress){
                        Platform.runLater(() -> this.progresses.get(index).setProgress((double) progress / 100));
                    }
                };
                launchDialog.setV(0, 1, Launcher.languageManager.get("ui.mainpage.launch._02"));

                JFXButton stopAction = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
                ThemeManager.loadButtonAnimates(stopAction);

                launchDialog.layout.setActions(stopAction);
                AtomicReference<Launch> core = new AtomicReference<>(null);
                Thread la = new Thread(() -> {
                    try {
                        if (new File(Launcher.configReader.configModel.selected_java_index).exists()) {
                            game.add(new Launch());
                            Thread.currentThread().setName(String.format("Launch Thread #%d", game.size() - 1));
                            core.set(game.get(game.size() - 1));
                            game.get(game.size() - 1).launch(
                                    Launcher.configReader.configModel.selected_java_index, Launcher.configReader.configModel.selected_minecraft_dir_index, Launcher.configReader.configModel.selected_version_index, Launcher.configReader.configModel.change_game_dir,
                                    Launcher.configReader.configModel.max_memory,
                                    UserSelectPage.user_object.get());
                            logger.info("started launch thread");
                        } else {
                            Launcher.configReader.configModel.selected_java.remove(Launcher.configReader.configModel.selected_java_index);
                            Launcher.configReader.configModel.selected_java_index = "";
                            Launcher.configReader.write();
                            Platform.runLater(() -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.mainpage.launch.javaChecker.name"), Launcher.languageManager.get("ui.mainpage.launch.javaChecker.Headcontent"), ""));
                        }
                    }
                    catch (Exception e) {
                        launchDialog.close();
                        logger.error("failed to launch", e);
                        Platform.runLater(() -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.mainpage.launch.launchFailed.name"), Launcher.languageManager.get("ui.mainpage.launch.launchFailed.Headcontent"), e.toString()));
                    }
                });
                stopAction.setOnAction(event1 -> {
                    la.stop();
                    if (core.get() != null) core.get().stop_process();
                    launchDialog.close();
                });
                la.start();
            } else {
                if (launchDialog != null) launchDialog.close();
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.mainpage.launch.noVersion.name"), Launcher.languageManager.get("ui.mainpage.launch.noVersion.Headcontent"), Launcher.languageManager.get("ui.mainpage.launch.noVersion.content"));
            }
        });

        stopProcess = new JFXButton();
        stopProcess.setId("launch-button");
        stopProcess.setFont(Fonts.s_f);
        stopProcess.setTextFill(Color.WHITE);
        ListChangeListener<Launch> listener = c -> {
            stopProcess.setDisable(game.size() == 0);
            BGMManager.startOrStop(game.size() == 0);
        };
        game.addListener(listener);
        listener.onChanged(new ListChangeListener.Change<Launch>(game) {
            public boolean next() {return false;}
            public void reset() {}
            public int getFrom() {return 0;}
            public int getTo() {return 0;}
            public List<Launch> getRemoved() {return null;}
            protected int[] getPermutation() {return new int[0];}
        });

        stopProcess.setOnAction(event -> stopAllProcess());

        launchBox = new VBox();
        launchBox.setAlignment(Pos.BOTTOM_LEFT);
        launchBox.setMaxSize(width / 2, height - 185);
        launchBox.getChildren().addAll(stopProcess, launchButton);

        title = new Label();
        launch = new Label();
        set = new Label();
        choose_version = new JFXButton();
        version_settings = new JFXButton();
        settings = new JFXButton();
        downloadMc = new JFXButton();
        downloadTitle = new Label();
        users = new JFXButton();

        title.setFont(Fonts.b_f);
        launch.setFont(Fonts.t_f);
        set.setFont(Fonts.t_f);
        choose_version.setFont(Fonts.s_f);
        version_settings.setFont(Fonts.s_f);
        settings.setFont(Fonts.s_f);
        downloadMc.setFont(Fonts.s_f);
        downloadTitle.setFont(Fonts.t_f);
        users.setFont(Fonts.s_f);

        settings.setOnAction(event -> Launcher.setPage(Launcher.CONFIGPAGE, this));
        version_settings.setOnAction(event -> Launcher.setPage(Launcher.VERSIONINFOPAGE, this));
        downloadMc.setOnAction(event -> Launcher.setPage(Launcher.DOWNLOADMCPAGE, this));
        users.setOnAction(event -> Launcher.setPage(Launcher.USERSELECTPAGE, this));

        is_vaild_minecraft_dir = Launcher.configReader.configModel.selected_minecraft_dir.contains(Launcher.configReader.configModel.selected_minecraft_dir_index) && new File(Launcher.configReader.configModel.selected_minecraft_dir_index).exists();

        choose_version.setOnAction(event -> {
            l = new ProcessDialog(1, Launcher.languageManager.get("ui.versionListLoad._02"));
            l.setV(0, 0, Launcher.languageManager.get("ui.mainpage.loadlist.name"));
            Launcher.setPage(Launcher.VERSIONSELECTPAGE, this);
        });

        version_settings.setGraphic(Launcher.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        settings.setGraphic(Launcher.getSVGManager().gear(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));
        downloadMc.setGraphic(Launcher.getSVGManager().downloadOutline(Bindings.createObjectBinding(this::returnBlack), 25.0D, 25.0D));

        LaunchTitle = new HBox();
        LaunchTitle.setAlignment(Pos.BOTTOM_CENTER);
        LaunchTitle.getChildren().add(launch);

        SetTitle = new HBox();
        SetTitle.setAlignment(Pos.BOTTOM_CENTER);
        SetTitle.getChildren().add(set);

        choose_version.setButtonType(JFXButton.ButtonType.RAISED);
        version_settings.setButtonType(JFXButton.ButtonType.RAISED);
        settings.setButtonType(JFXButton.ButtonType.RAISED);
        launchButton.setButtonType(JFXButton.ButtonType.RAISED);
        downloadMc.setButtonType(JFXButton.ButtonType.RAISED);
        users.setButtonType(JFXButton.ButtonType.RAISED);

        choose_version.setMaxWidth(width / 4);
        version_settings.setMaxWidth(width / 4);
        settings.setMaxWidth(width / 4);
        downloadMc.setMaxWidth(width / 4);
        users.setMaxWidth(width / 4);

        GameMenu = new VBox();
        GameMenu.setId("game-menu");
        FXUtils.ControlSize.set(GameMenu, width / 4, height);
        GameMenu.setAlignment(Pos.TOP_CENTER);

        GameMenu.getChildren().addAll(
                title,
                LaunchTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                choose_version,
                users,
                new Spacer(),
                SetTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                version_settings,
                settings,
                new Spacer(),
                downloadTitle,
                FXUtils.ControlSize.setSplit(new SplitPane(), width / 4 - 20),
                new Spacer(),
                downloadMc
        );

        Pane p = new Pane();
        FXUtils.ControlSize.set(p, width / 2, height - Launcher.barSize - 50);

        this.add(GameMenu, 0, 1, 1, 1);
        this.add(p, 1, 1, 1, 1);
        this.add(launchBox, 2, 1, 1, 1);

        this.setAlignment(Pos.TOP_LEFT);

        new Thread(() -> {
            while (true) {
                flush();
                Sleeper.sleep(10);
            }
        }).start();
    }
    public static void check(Launch launchCore){
        Platform.runLater(launchDialog::close);
        if (launchCore.exitCode != null){
            logger.info("Minecraft exited with code " + launchCore.exitCode);
            if (launchCore.exitCode != 0){

                SimpleDialogCreater.create(Launcher.languageManager.get("ui.mainpage.minecraftExit.title"), String.format(Launcher.languageManager.get("ui.mainpage.minecraftExit.Headercontent"), launchCore),String.format(Launcher.languageManager.get("ui.mainpage.minecraftExit.content"), launchCore.exitCode));
            }
        }
    }
    public static class Spacer extends Label {
        public Spacer(){
            super();
            this.setText("");
        }
    }
    public void clean_null_version(){
        Platform.runLater(() -> {
            version_settings.setText(Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"));
            launchButton.setText(Launcher.languageManager.get("ui.mainpage.launchButton.noVersion"));
            Launcher.configReader.configModel.selected_version_index = "";
            Launcher.configReader.write();
            version_settings.setDisable(true);
        });
    }
    public void flush(){
        boolean minecraft_dir_exists = new File(Launcher.configReader.configModel.selected_minecraft_dir_index).exists();

        if (minecraft_dir_exists) {
            if (Launcher.configReader.configModel.selected_minecraft_dir.contains(Launcher.configReader.configModel.selected_minecraft_dir_index)) {
                if (Launcher.configReader.configModel.selected_version_index != null) {
                    if (Objects.requireNonNull(getMinecraftVersion.get(Launcher.configReader.configModel.selected_minecraft_dir_index)).contains(Launcher.configReader.configModel.selected_version_index)) {
                        if (new File(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions/%s/%s.json", Launcher.configReader.configModel.selected_version_index, Launcher.configReader.configModel.selected_version_index)).exists()) {
                            if (!Launcher.configReader.configModel.selected_version_index.equals("")) {
                                if (JsonUtils.isVaildJson(new File(Launcher.configReader.configModel.selected_minecraft_dir_index, String.format("versions/%s/%s.json", Launcher.configReader.configModel.selected_version_index, Launcher.configReader.configModel.selected_version_index)))) {
                                    Platform.runLater(() -> {
                                        version_settings.setText(" " + Launcher.configReader.configModel.selected_version_index);
                                        launchButton.setText(Launcher.languageManager.get("ui.mainpage.launchButton.hasVersion"));
                                        version_settings.setDisable(false);
                                        downloadMc.setDisable(false);
                                    });
                                }
                                else {
                                    clean_null_version();
                                    downloadMc.setDisable(false);
                                }
                            }
                            else {
                                clean_null_version();
                                downloadMc.setDisable(false);
                            }
                        }
                        else {
                            clean_null_version();
                            downloadMc.setDisable(false);
                        }
                    } else {
                        clean_null_version();
                        downloadMc.setDisable(false);
                    }
                }
                else{
                    clean_null_version();
                    downloadMc.setDisable(false);
                }
            }
            else{
                clean_null_version();
                downloadMc.setDisable(true);
            }
        }
        else{
            clean_null_version();
            downloadMc.setDisable(true);
        }
    }
    public void refresh(){
        flush();
    }
    public void refreshType(){

    }

    public void onExitPage() {

    }

    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.mainpage.name");
        title.setText(String.format(Launcher.languageManager.get("ui.title"), VersionInfo.launcher_name, VersionInfo.launcher_version));
        launch.setText(Launcher.languageManager.get("ui.mainpage.launchTitle.launch.name"));
        set.setText(Launcher.languageManager.get("ui.mainpage.settings.name"));
        choose_version.setText(Launcher.languageManager.get("ui.mainpage.choose_version.name"));
        settings.setText(" "+ Launcher.languageManager.get("ui.mainpage.settings.name"));
        downloadTitle.setText(Launcher.languageManager.get("ui.mainpage.download.title"));
        downloadMc.setText(Launcher.languageManager.get("ui.mainpage.downloadMc.name"));
        users.setText(Launcher.languageManager.get("ui.mainpage.users.name"));
        stopProcess.setText(Launcher.languageManager.get("ui.mainpage.stop"));
    }
}