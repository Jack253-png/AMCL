package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.controls.VanilaVersionContent;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadMcPage extends AbstractAnimationPage {
    VBox mainBox;
    VBox menu;
    JFXButton setting;
    JFXButton load;
    SettingPage last;
    public SettingPage p1;
    Parent setted;
    public Vector<TitledPane> panes = new Vector<>();
    com.jfoenix.controls.JFXProgressBar bar;
    Thread service;
    public DownloadMcPage(int width, int height){
        super(width, height);
        l = Launcher.MAINPAGE;
        this.setAlignment(Pos.TOP_CENTER);

        double t_size = Launcher.barSize;

        bar = JFXProgressBar.createProgressBar(-1.0D);
        FXUtils.ControlSize.setWidth(bar, this.width / 4 * 3);

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
        load = new JFXButton();
        load.setFont(Fonts.s_f);
        load.setOnAction(event -> loadVersions());
        load.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(this::returnBlack), 20, 20));
        setting.setButtonType(JFXButton.ButtonType.RAISED);
        load.setButtonType(JFXButton.ButtonType.RAISED);
        FXUtils.ControlSize.setWidth(setting, this.width / 4 / 4 * 3);
        FXUtils.ControlSize.setWidth(load, this.width / 4 / 4);

        HBox box = new HBox(setting, load);

        menu = new VBox();
        menu.setId("config-menu");
        menu.getChildren().addAll(box);
        FXUtils.ControlSize.set(menu, this.width / 4,this.height - t_size);
        loadVersions();
        setP1(p1);
        setType(setting);
    }
    public void loadVersions(){
        if (service != null) service.stop();
        Runnable r = () -> {
            load.setDisable(true);
            mainBox.setDisable(true);
            Platform.runLater(mainBox.getChildren()::clear);
            Platform.runLater(() -> mainBox.getChildren().add(bar));
            mainBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.50)");
            Vector<OriginalVersionModel> vs = GetVersionList.getOriginalList();
            Vector<String> types = new Vector<>();
            for (OriginalVersionModel m : vs){
                if (!types.contains(m.type)){
                    types.add(m.type);
                }
            }
            Platform.runLater(() -> bar.setProgress(-1.0D));
            AtomicInteger loaded = new AtomicInteger();
            for (String t : types){
                TitledPane pane = new TitledPane();
                pane.getStylesheets().add(String.format(ThemeManager.getPath(), "TitledPane"));
                panes.add(pane);
                pane.setText(Launcher.languageManager.get("ui.downloadmcpage.types." + t));
                pane.setFont(Fonts.s_f);
                FXUtils.ControlSize.setWidth(pane, DownloadMcPage.width / 4 * 3);
                SmoothableListView<VanilaVersionContent> listv = new SmoothableListView<>(DownloadMcPage.width / 4 * 3, 300);
                listv.page.getStylesheets().add(String.format(ThemeManager.getPath(), "JFXListView"));
                listv.setStyle("-fx-background-color: transparent");
                Platform.runLater(() -> pane.setContent(listv.page));
                vs.forEach(model -> {
                    if (Objects.equals(model.type, t)){
                        loaded.addAndGet(1);
                        listv.addItem(new VanilaVersionContent(model));
                        Platform.runLater(() -> bar.setProgress(((double) loaded.get()) * 100 / vs.size()));
                    }
                });
                FXUtils.ControlSize.setWidth(listv, DownloadMcPage.width / 3 * 2);
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
                listv.getChildren().sort((o1, o2) -> 0);
                listv.setOnAction(() -> {
                    Launcher.setPage(Launcher.DOWNLOADADDONSELECTPAGE, this);
                    Launcher.DOWNLOADADDONSELECTPAGE.setVersionId(listv.selectedItem.model);
                });
                pane.getStyleClass().clear();
                Platform.runLater(() -> mainBox.getChildren().add(pane));
                Platform.runLater(() -> ThemeManager.loadButtonAnimates(pane));
                mainBox.setDisable(false);
            }
            load.setDisable(false);
        };
        service = new Thread(r);
        service.start();
    }
    public void setType(Parent b){
        setted = b;
        for (Node bs : menu.getChildren()){
            if (bs instanceof HBox){
                for (Node n : ((HBox) bs).getChildren()) {
                    n.setDisable(n == b);
                }
            }
            bs.setDisable(bs == b);
        }
    }
    public void setP1(SettingPage p){
        if (p.CanMovePage() && last != p) {
            load.setDisable(p != p1);
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
            FXUtils.ControlSize.set(mainBox, this.width / 4 * 3, this.height - Launcher.barSize);
            this.add(menu, 0, 0, 1, 1);
            this.add(mainBox, 1, 0, 1, 1);
        }
    }
    public void refresh(){
        p1.set(this.opacityProperty());
        setType(setted);
        loadVersions();
    }
    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.downloadmcpage.name");
        setting.setText(Launcher.languageManager.get("ui.downloadmcpage.menu._01"));
    }
    public void refreshType(){

    }

    public void onExitPage() {
        service.stop();
    }
}
