package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.controls.VanilaVersionContent;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.SetSize;
import com.mcreater.amcl.util.multiThread.Run;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;

public class DownloadMcPage extends AbstractAnimationPage {
    VBox mainBox;
    VBox menu;
    JFXButton setting;
    JFXButton load;
    SettingPage last;
    SettingPage p1;
    SettingPage p2;
    JFXButton setted;
    Vector<TitledPane> panes = new Vector<>();
    JFXProgressBar bar;
    public DownloadMcPage(int width, int height){
        super(width, height);
        l = Application.MAINPAGE;
        set();
        this.setAlignment(Pos.TOP_CENTER);

        double t_size = Application.barSize;

        bar = new JFXProgressBar(-1.0D);
        SetSize.setWidth(bar, this.width / 4 * 3);

        mainBox = new VBox();
        mainBox.getChildren().add(bar);

        p1 = new SettingPage(this.width / 4 * 3, this.height - t_size, mainBox);

        last = null;

        setting = new JFXButton();
        setting.setFont(Fonts.s_f);
        setting.setOnAction(event -> {
            setP1(p1);
            setType(setting);
        });
        load = new JFXButton("test");
        load.setFont(Fonts.s_f);
        load.setOnAction(event -> {
            load.setDisable(true);
            loadVersions();
        });
        SetSize.setWidth(setting, this.width / 4);
        SetSize.setWidth(load, this.width / 4);

        menu = new VBox();
        menu.setId("config-menu");
        menu.getChildren().addAll(setting, load);
        SetSize.set(menu, this.width / 4,this.height - t_size);
        loadVersions();
        setP1(p1);
        setType(setting);
    }
    public void loadVersions(){
        Runnable r = () -> {
            Platform.runLater(mainBox.getChildren()::clear);
            Platform.runLater(() -> mainBox.getChildren().add(bar));
            mainBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.75)");
            Vector<OriginalVersionModel> vs = GetVersionList.getOriginalList(true);
            Vector<String> types = new Vector<>();
            for (OriginalVersionModel m : vs){
                if (!types.contains(m.type)){
                    types.add(m.type);
                }
            }
            for (String t : types){
                TitledPane pane = new TitledPane();
                pane.getStylesheets().add(String.format(ThemeManager.getPath(), "TitledPane"));
                panes.add(pane);
                pane.setText(Application.languageManager.get("ui.downloadmcpage.types." + t));
                pane.setFont(Fonts.s_f);
                SetSize.setWidth(pane, DownloadMcPage.width / 4 * 3);
                JFXListView<VanilaVersionContent> listv = new JFXListView<>();
                listv.getStylesheets().add(String.format(ThemeManager.getPath(), "JFXListView"));
                vs.forEach(model -> {
                    if (Objects.equals(model.type, t)){
                        listv.getItems().add(new VanilaVersionContent(model));
                    }
                });
                SetSize.setWidth(listv, DownloadMcPage.width / 4 * 3);
                Platform.runLater(() -> pane.setContent(listv));
                pane.setExpanded(false);
                pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue){
                        for (TitledPane p : panes){
                            if (p != pane){
                                p.setExpanded(false);
                            }
                        }
                    }
                });
                listv.getItems().sort((node, t1) -> 0);
                Platform.runLater(() -> mainBox.getChildren().add(pane));
            }
            load.setDisable(false);
        };
        Service<String> service = Run.run(r);
        service.start();
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
            SetSize.set(mainBox, this.width / 4 * 3, this.height - Application.barSize);
            this.add(menu, 0, 0, 1, 1);
            this.add(mainBox, 1, 0, 1, 1);
        }
    }
    public void refresh(){
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
        setType(setted);
    }
    public void refreshLanguage(){
        name = Application.languageManager.get("ui.downloadmcpage.name");
        setting.setText(Application.languageManager.get("ui.downloadmcpage.menu._01"));
    }
    public void refreshType(){

    }

    public void onExitPage() {

    }
}
