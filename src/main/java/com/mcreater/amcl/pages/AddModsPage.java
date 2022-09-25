package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.CurseResourceType;
import com.mcreater.amcl.api.curseApi.CurseSortType;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.controls.CurseMod;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Vector;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class AddModsPage extends AbstractAnimationPage {
    GridPane pane;
    public JFXTextField in;
    public JFXButton submit;
    public SmoothableListView<CurseMod> modlist;
    public com.jfoenix.controls.JFXProgressBar bar;
    Thread searchThread = new Thread(() -> {});
    public AddModsPage(double width, double height) {
        super(width, height);
        l = Launcher.VERSIONINFOPAGE;
        pane = new GridPane();
        FXUtils.ControlSize.set(pane, this.width, this.height);
        in = new JFXTextField();
        FXUtils.fixJFXTextField(in);
        submit = new JFXButton();
        bar = JFXProgressBar.createProgressBar(0);
        in.setFont(Fonts.t_f);
        submit.setFont(Fonts.t_f);
        submit.setButtonType(JFXButton.ButtonType.RAISED);
        FXUtils.ControlSize.set(in, this.width / 8 * 7, 45);
        FXUtils.ControlSize.set(submit, this.width / 8, 45);
        FXUtils.ControlSize.set(bar, this.width, 10);
        modlist = new SmoothableListView<>(width, height - 45 - 10 - Launcher.barSize);
        modlist.onReleasedProperty.set(() -> {
            if (modlist.selectedItem != null) {
                try {
                    showDownloads(modlist.selectedItem.model);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        modlist.page.setStyle("-fx-background-color: transparent");


        submit.setOnAction(event -> search());
        pane.setAlignment(Pos.TOP_CENTER);
        add(in, 0, 0, 1, 1);
        add(submit, 1, 0, 1, 1);
        add(modlist.page, 0, 2, 2, 1);
        add(bar, 0, 3, 2, 1);
        setStyle("-fx-background-color : rgba(255, 255, 255, 0.50)");
        nodes.add(null);
        BindedPageproperty().get().addAll(J8Utils.createList(
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
    public void search(){
        searchThread.stop();
        searchThread = new Thread(() -> {
            submit.setDisable(true);
            try {
                this.searchMods();
            } catch (IOException e) {
                Platform.runLater(() -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.addmodspage.loadmods.fail.title"), String.format(Launcher.languageManager.get("ui.addmodspage.loadmods.fail.content"), e), ""));
            }
            finally {
                submit.setDisable(false);
            }
        });
        searchThread.start();
    }
    public void searchMods() throws IOException {
        Platform.runLater(() -> modlist.setDisable(true));
        Platform.runLater(modlist::clear);
        JFXSmoothScroll.smoothScrollBarToValue(bar, -1);
        Vector<CurseModModel> mods = CurseAPI.search(in.getText(), CurseResourceType.Types.MOD, CurseSortType.Types.DESCENDING, 20);
        double loaded = 0;
        for (CurseModModel model : mods){
            loaded += 1;
            CurseMod m = new CurseMod(model);
            Platform.runLater(() -> modlist.addItem(m));
            double finalLoaded = loaded;
            JFXSmoothScroll.smoothScrollBarToValue(bar, finalLoaded / mods.size());
            ThemeManager.loadButtonAnimates(m);
            Sleeper.sleep(150);
        }
        Platform.runLater(() -> modlist.setDisable(false));
    }
    public void showDownloads(CurseModModel model) throws InterruptedException {
        Launcher.MODDOWNLOADPAGE.setModContent(model);
        Launcher.setPage(Launcher.MODDOWNLOADPAGE, this);
        new Thread(() -> {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void refresh() {

    }
    public void refreshLanguage() {
        this.name = Launcher.languageManager.get("ui.addmodspage.name");
        submit.setText(Launcher.languageManager.get("ui.addmodspage.search.name"));
        for (CurseMod m : modlist.vecs){
            m.refreshLang();
        }
    }
    public void refreshType() {

    }

    public void onExitPage() {
        searchThread.stop();
    }
}
