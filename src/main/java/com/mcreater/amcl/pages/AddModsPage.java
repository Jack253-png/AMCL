package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.CurseResourceType;
import com.mcreater.amcl.api.curseApi.CurseSortType;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.controls.CurseMod;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Vector;

public class AddModsPage extends AbstractAnimationPage {
    GridPane pane;
    JFXTextField in;
    JFXButton submit;
    JFXListView<CurseMod> modlist;
    JFXProgressBar bar;
    Thread searchThread;
    public AddModsPage(double width, double height) {
        super(width, height);
        l = Application.VERSIONINFOPAGE;
        set();
        pane = new GridPane();
        SetSize.set(pane, this.width, this.height);
        in = new JFXTextField();
        submit = new JFXButton();
        bar = new JFXProgressBar(0);
        in.setFont(Fonts.t_f);
        submit.setFont(Fonts.t_f);
        BorderStroke borderStroke = new BorderStroke(null,null, Color.BLACK,null, null,null, BorderStrokeStyle.SOLID,null,null, BorderWidths.DEFAULT,new Insets(5));
        in.setBorder(new Border(borderStroke));
        submit.setButtonType(JFXButton.ButtonType.RAISED);
        SetSize.set(in, this.width / 8 * 7, 45);
        SetSize.set(submit, this.width / 8, 45);
        SetSize.setWidth(bar, this.width);
        modlist = new JFXListView<>();
        modlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    showDownloads(newValue.model);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        SetSize.setHeight(modlist, this.height - 45 - 45 - 2);
        searchThread = new Thread(() -> {
            submit.setDisable(true);
            try {
                this.searchMods();
            } catch (IOException e) {
                Platform.runLater(() -> FastInfomation.create(Application.languageManager.get("ui.addmodspage.loadmods.fail.title"), String.format(Application.languageManager.get("ui.addmodspage.loadmods.fail.content"), e), ""));
            }
            finally {
                submit.setDisable(false);
            }
        });
        submit.setOnAction(event -> searchThread.start());

        pane.setAlignment(Pos.TOP_CENTER);
        pane.add(in, 0, 0, 1, 1);
        pane.add(submit, 1, 0, 1, 1);
        pane.add(bar, 0, 2, 2, 1);
        pane.add(modlist, 0, 3, 2, 1);
        pane.setStyle("-fx-background-color : rgba(255, 255, 255, 0.75)");
        this.add(pane, 0, 0, 1, 1);
    }
    public void searchMods() throws IOException {
        Platform.runLater(modlist.getItems()::clear);
        Platform.runLater(() -> bar.setProgress(0));
        Vector<CurseModModel> mods = CurseAPI.search(in.getText(), CurseResourceType.Types.MOD, CurseSortType.Types.DESCENDING, 20);
        double loaded = 0;
        for (CurseModModel model : mods){
            loaded += 1;
            CurseMod m = new CurseMod(model);
            Platform.runLater(() -> modlist.getItems().addAll(m));
            double finalLoaded = loaded;
            Platform.runLater(() -> bar.setProgress(finalLoaded / mods.size()));
        }
    }
    public void showDownloads(CurseModModel model) throws InterruptedException {
        Application.setPage(Application.MODDOWNLOADPAGE, this);
        Application.MODDOWNLOADPAGE.setModContent(model);
        new Thread(() -> {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            modlist.getSelectionModel().clearSelection();
        }).start();
    }
    public void refresh() {

    }
    public void refreshLanguage() {
        this.name = Application.languageManager.get("ui.addmodspage.name");
        submit.setText(Application.languageManager.get("ui.addmodspage.search.name"));
    }
    public void refreshType() {

    }

    public void onExitPage() {
        searchThread.stop();
    }
}
