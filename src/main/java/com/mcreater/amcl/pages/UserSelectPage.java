package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.AccountInfoItem;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.account.OfflineUserCreateDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class UserSelectPage extends AbstractAnimationPage {
    VBox sideBar;
    SmoothableListView<AccountInfoItem> userList;
    AdvancedScrollPane page1;

    JFXButton menuButtonOffline;
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;

        userList = new SmoothableListView<>(width / 4 * 3, height - Launcher.barSize);
        reloadUser();

        menuButtonOffline = new JFXButton();
        menuButtonOffline.setFont(Fonts.s_f);
        menuButtonOffline.setOnAction(event -> {
            OfflineUserCreateDialog dialog = new OfflineUserCreateDialog(Launcher.languageManager.get("ui.userselectpage._01.name"));
            dialog.setCancel(event1 -> FXUtils.Platform.runLater(dialog::close));
            dialog.setCreate(event2 -> {
                int type = dialog.getSelected();
                switch (type) {
                    default:
                        return;
                    case 0:
                        Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.STEVE.uuid, false, null, null));
                        dialog.close();
                        reloadUser();
                        break;
                    case 1:
                        Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.ALEX.uuid, true, null, null));
                        dialog.close();
                        reloadUser();
                        break;
                }
            });
            dialog.Create();
        });

        Pane paneContainer = new Pane(userList.page);

        page1 = new AdvancedScrollPane(width / 4 * 3, height, paneContainer, false);

        sideBar = new VBox(menuButtonOffline);
        sideBar.setId("config-menu");

        FXUtils.ControlSize.set(sideBar, width / 4, height);
        FXUtils.ControlSize.setWidth(menuButtonOffline, width / 4);

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
        setAlignment(Pos.TOP_LEFT);
        add(sideBar, 0, 0, 1, 1);
        add(page1, 1, 0, 1, 1);
    }
    public void checkActiveState() {
        AbstractUser userTarget = null;
        for (AbstractUser user : Launcher.configReader.configModel.accounts) {
            if (user.active) {
                if (userTarget == null) userTarget = user;
                else {
                    user.active = false;
                }
            }
        }
        if (userTarget == null) {
            if (Launcher.configReader.configModel.accounts.size() > 0) {
                Launcher.configReader.configModel.accounts.get(0).active = true;
            }
        }
        Launcher.configReader.write();
    }
    public void reloadUser() {
        Runnable runn = () -> new Thread(() -> {
            checkActiveState();
            for (AbstractUser user : Launcher.configReader.configModel.accounts) {
                AccountInfoItem item = new AccountInfoItem(user, width / 3 * 2);
                item.selector.setSelected(user.active);
                item.selector.setOnAction(event -> {
                    if (!item.selector.isSelected()) item.selector.setSelected(true);
                    userList.vecs.forEach(accountInfoItem -> {
                        if (accountInfoItem != item) accountInfoItem.selector.setSelected(false);
                    });
                    Launcher.configReader.write();
                });

                item.setDelete(event -> {
                    Launcher.configReader.configModel.accounts.remove(item.user);
                    Launcher.configReader.write();
                    Timeline out = new Timeline();
                    out.setCycleCount(1);
                    out.getKeyFrames().clear();
                    out.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(item.opacityProperty(), 1)));
                    out.getKeyFrames().add(new KeyFrame(new Duration(300), new KeyValue(item.opacityProperty(), 0)));
                    out.setOnFinished(event1 -> reloadUser());
                    out.play();
                });
                FXUtils.Platform.runLater(() -> userList.addItem(item));
            }

            Launcher.configReader.write();

            FXUtils.Platform.runLater(() -> userList.setDisable(false));

            Timeline out2 = new Timeline();
            out2.setCycleCount(1);
            out2.getKeyFrames().clear();
            out2.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(userList.page.opacityProperty(), 0)));
            out2.getKeyFrames().add(new KeyFrame(new Duration(300), new KeyValue(userList.page.opacityProperty(), 1)));
            out2.play();
        }).start();

        Timeline out1 = new Timeline();
        out1.setCycleCount(1);
        out1.getKeyFrames().clear();
        out1.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(userList.page.opacityProperty(), 1)));
        out1.getKeyFrames().add(new KeyFrame(new Duration(300), new KeyValue(userList.page.opacityProperty(), 0)));
        out1.setOnFinished(event1 -> {
            userList.clear();
            userList.setDisable(true);
            runn.run();
        });
        out1.play();
    }

    public void refresh() {

    }

    public void refreshLanguage() {
        menuButtonOffline.setText(Launcher.languageManager.get("ui.userselectpage._01.name"));
    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
