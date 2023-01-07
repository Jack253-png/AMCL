package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.modApi.common.AbstractModModel;
import com.mcreater.amcl.api.modApi.curseforge.CurseAPI;
import com.mcreater.amcl.api.modApi.curseforge.CurseResourceType;
import com.mcreater.amcl.api.modApi.curseforge.CurseSortType;
import com.mcreater.amcl.api.modApi.modrinth.ModrinthAPI;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.controls.ServerMod;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
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
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;

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
import static com.mcreater.amcl.util.FXUtils.ColorUtil.transparent;

public class AddModsPage extends AbstractAnimationPage {
    GridPane pane;
    public JFXButton submit;
    public ListItem<Label> server;
    public SmoothableListView<ServerMod> modlist;
    public com.jfoenix.controls.JFXProgressBar bar;

    StringItem item;
    Thread searchThread = new Thread(() -> {});
    public AddModsPage(double width, double height) {
        super(width, height);
        l = Launcher.VERSIONINFOPAGE;
        pane = new GridPane();
        FXUtils.ControlSize.set(pane, this.width, this.height);

        item = new StringItem("", width, true);

        submit = new JFXButton();
        bar = JFXProgressBar.createProgressBar(0);

        server = new ListItem<>("", width);
        Label curse = new Label("Curseforge");
        curse.setFont(Fonts.t_f);
        Label modri = new Label("Modrinth");
        modri.setFont(Fonts.t_f);
        server.cont.getItems().addAll(curse, modri);
        server.cont.getSelectionModel().selectFirst();

        item.cont.setFont(Fonts.t_f);
        submit.setFont(Fonts.t_f);
        submit.setButtonType(JFXButton.ButtonType.RAISED);
        FXUtils.ControlSize.set(item, this.width, 25);
        FXUtils.ControlSize.set(submit, this.width, 45);
        FXUtils.ControlSize.setHeight(server, 50);
        FXUtils.ControlSize.set(bar, this.width, 10);
        modlist = new SmoothableListView<>(width, height - 45 - 25 - 50 - 10 - Launcher.barSize);
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
        add(item, 0, 0, 1, 1);
        add(submit, 0, 2, 1, 1);
        add(server, 0, 1, 1, 1);
        add(modlist.page, 0, 3, 2, 1);
        add(bar, 0, 4, 2, 1);
        ThemeManager.addLis((observable, oldValue, newValue) -> setBackground(new Background(
                new BackgroundFill(
                        transparent(newValue, 0.5),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        )));
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
    public void search(){
        searchThread.stop();
        searchThread = new Thread(() -> {
            submit.setDisable(true);
            try {
                this.searchMods();
            } catch (IOException e) {
                SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.addmodspage.loadmods.fail.title"));
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
        Vector<? extends AbstractModModel> mods = server.cont.getSelectionModel().getSelectedIndex() == 0 ? CurseAPI.search(item.cont.getText(), CurseResourceType.Types.MOD, CurseSortType.Types.DESCENDING, 20) : ModrinthAPI.search(item.cont.getText(), 20);
        double loaded = 0;
        for (AbstractModModel model : mods){
            loaded += 1;
            ServerMod m = new ServerMod(model);
            Platform.runLater(() -> modlist.addItem(m));
            double finalLoaded = loaded;
            JFXSmoothScroll.smoothScrollBarToValue(bar, finalLoaded / mods.size());
            ThemeManager.loadNodeAnimations(m);
            Sleeper.sleep(150);
        }
        Platform.runLater(() -> modlist.setDisable(false));
    }
    public void showDownloads(AbstractModModel model) throws InterruptedException {
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
        server.name.setText(Launcher.languageManager.get("ui.moddownloadpage.modserver.name"));
        submit.setText(Launcher.languageManager.get("ui.addmodspage.search.name"));
        item.title.setText(Launcher.languageManager.get("ui.moddownloadpage.search.name"));
        for (ServerMod m : modlist.vecs){
            m.refreshLang();
        }
    }
    public void refreshType() {

    }

    public void onExitPage() {
        searchThread.stop();
    }
}
