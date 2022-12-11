package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.controls.VanilaVersionContent;
import com.mcreater.amcl.download.GetVersionList;
import com.mcreater.amcl.download.model.OriginalVersionModel;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FXUtils.Platform;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;
import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;
import static com.mcreater.amcl.util.FXUtils.ColorUtil.transparent;

public class DownloadMcPage extends AbstractAnimationPage {
    VBox mainBox;
    VBox menu;
    JFXButton setting;
    JFXButton load;
    AdvancedScrollPane last;
    public AdvancedScrollPane p1;
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

        p1 = new AdvancedScrollPane(this.width / 4 * 3, this.height - t_size, mainBox);

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
        setting.setButtonType(JFXButton.ButtonType.RAISED);
        load.setButtonType(JFXButton.ButtonType.RAISED);
        FXUtils.ControlSize.set(setting, this.width / 4 / 4 * 3, 40);
        FXUtils.ControlSize.set(load, this.width / 4 / 4, 40);

        HBox box = new HBox(setting, load);

        menu = new VBox();
        menu.setId("config-menu");
        menu.getChildren().addAll(box);
        FXUtils.ControlSize.set(menu, this.width / 4,this.height - t_size);
        loadVersions();
        setP1(p1);
        setType(setting);

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
    }
    public void loadVersions(){
        if (service != null) service.stop();
        Runnable r = () -> {
            load.setDisable(true);
            mainBox.setDisable(true);
            Platform.runLater(mainBox.getChildren()::clear);
            Platform.runLater(() -> mainBox.getChildren().add(bar));
            mainBox.setId("verList");
            Vector<OriginalVersionModel> vs;
            try {
                vs = GetVersionList.getOriginalList(FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer));
            } catch (Exception e) {
                load.setDisable(false);
                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.downloadaddonsselectpage.fail.title"));
                return;
            }
            Vector<String> types = new Vector<>();
            for (OriginalVersionModel m : vs) {
                if (!types.contains(m.type)) {
                    types.add(m.type);
                }
            }
            JFXSmoothScroll.smoothScrollBarToValue(bar, -1.0D);
            AtomicInteger loaded = new AtomicInteger();
            for (String t : types) {
                TitledPane pane = new TitledPane();

                ThemeManager.applyNode(pane);
                pane.setTextFill(reverse(ThemeManager.themeIconDark.get()));
                panes.add(pane);
                pane.setText(Launcher.languageManager.get("ui.downloadmcpage.types." + t));
                pane.setFont(Fonts.s_f);
                FXUtils.ControlSize.setWidth(pane, width / 4 * 3);
                SmoothableListView<VanilaVersionContent> listv = new SmoothableListView<>(width / 4 * 3, 300);
                listv.setStyle("-fx-background-color: transparent");
                Platform.runLater(() -> pane.setContent(listv.page));
                vs.forEach(model -> {
                    if (Objects.equals(model.type, t)) {
                        loaded.addAndGet(1);
                        listv.addItem(new VanilaVersionContent(model));
                        JFXSmoothScroll.smoothScrollBarToValue(bar, ((double) loaded.get()) * 100 / vs.size());
                    }
                });
                FXUtils.ControlSize.setWidth(listv, width / 3 * 2);
                pane.setExpanded(false);
                pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        for (TitledPane p : panes) {
                            if (p != pane) {
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
    public void setP1(AdvancedScrollPane p){
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
        load.setGraphic(Launcher.getSVGManager().refresh(ThemeManager.createPaintBinding(), 30, 30));
    }

    public void onExitPage() {
        service.stop();
    }
}
