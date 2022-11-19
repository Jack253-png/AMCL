package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.AccountInfoItem;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.account.OfflineUserCreateDialog;
import com.mcreater.amcl.pages.dialogs.account.OfflineUserCustomSkinDialog;
import com.mcreater.amcl.pages.dialogs.commons.InputDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
    JFXButton refreshList;
    public static final SimpleObjectProperty<AbstractUser> user = new SimpleObjectProperty<>();
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;

        userList = new SmoothableListView<>(width / 4 * 3, height - Launcher.barSize);

        refreshList = new JFXButton();
        refreshList.setFont(Fonts.s_f);
        refreshList.setOnAction(event -> reloadUser());

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
                    case 2:
                        InputDialog dialog1 = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input")) {
                            public void outAnimation() {

                            }
                        };
                        dialog1.setCancel(event12 -> dialog1.close());
                        dialog1.setEvent(event13 -> {
                            dialog1.close();
                            LoadingDialog dialog2 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.offline.id")) {
                                public void outAnimation() {

                                }
                            };
                            dialog2.Create();
                            new Thread(() -> {
                                String uuid = OffLineUser.SkinType.STEVE.uuid;
                                boolean isSlim = false;
                                try {
                                    uuid = MSAuth.getUserUUID(dialog1.f.getText());
                                    isSlim = MSAuth.getUserSkin(uuid).skin.isSlim;
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), uuid, isSlim, null, null));
                                FXUtils.Platform.runLater(() -> {
                                    dialog2.close();
                                    dialog.close();
                                });
                                reloadUser();
                            }).start();
                        });
                        dialog1.Create();
                    case 3:
                        OfflineUserCustomSkinDialog dialog2 = new OfflineUserCustomSkinDialog(Launcher.languageManager.get("ui.userselectpage.login.offlineskin")) {
                            public void outAnimation() {

                            }
                        };
                        dialog2.setCancel(event14 -> dialog2.close());
                        dialog2.setEvent(event15 -> {
                            Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), dialog2.getSelectedModelType() == 0 ? OffLineUser.SkinType.STEVE.uuid : OffLineUser.SkinType.ALEX.uuid, dialog2.getSelectedModelType() != 0, dialog2.getSkinPath(), dialog2.getCapePath()));
                            dialog.close();
                            dialog2.close();
                            reloadUser();
                        });
                        dialog2.Create();
                }
            });
            dialog.Create();
        });

        Pane paneContainer = new Pane(userList.page);

        page1 = new AdvancedScrollPane(width / 4 * 3, height, paneContainer, false);

        sideBar = new VBox(menuButtonOffline, refreshList);
        sideBar.setId("config-menu");

        FXUtils.ControlSize.set(sideBar, width / 4, height);
        FXUtils.ControlSize.setWidth(menuButtonOffline, width / 4);
        FXUtils.ControlSize.setWidth(refreshList, width / 4);

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
                userTarget = Launcher.configReader.configModel.accounts.get(0);
            }
        }
        user.set(userTarget);
        Launcher.configReader.write();
    }
    public void reloadUser() {
        Runnable runn = () -> new Thread(() -> {
            checkActiveState();
            for (AbstractUser user : Launcher.configReader.configModel.accounts) {
                AccountInfoItem item = new AccountInfoItem(user, width / 3 * 2);
                item.selector.setSelected(user.active);
                item.selector.setOnAction(event -> {
                    if (!item.selector.isSelected()) {
                        item.selector.setSelected(true);
                    }
                    userList.vecs.forEach(accountInfoItem -> {
                        if (accountInfoItem != item) accountInfoItem.selector.setSelected(false);
                    });
                    Launcher.configReader.write();
                });
                item.selector.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) UserSelectPage.user.set(item.user);
                });

                item.setDelete(event -> {
                    Launcher.configReader.configModel.accounts.remove(item.user);
                    Launcher.configReader.write();
                    FXUtils.AnimationUtils.runSingleCycleAnimation(userList.page.opacityProperty(), 1, 0, 100, 300, event1 -> reloadUser());
                });
                item.setRefresh(event -> {
                    LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.refresh.title"));
                });
                FXUtils.Platform.runLater(() -> userList.addItem(item));
            }

            Launcher.configReader.write();

            FXUtils.Platform.runLater(() -> {
                userList.page.setDisable(false);
                userList.setDisable(false);
                refreshList.setDisable(false);
            });

            FXUtils.AnimationUtils.runSingleCycleAnimation(userList.page.opacityProperty(), 0, 1, 100, 300, event1 -> {});
        }).start();

        FXUtils.AnimationUtils.runSingleCycleAnimation(userList.page.opacityProperty(), 1, 0, 100, 300, event1 -> {
            userList.page.setDisable(true);
            userList.setDisable(true);
            refreshList.setDisable(true);
            userList.clear();
            runn.run();
        });
    }

    public void refresh() {

    }

    public void refreshLanguage() {
        menuButtonOffline.setText(Launcher.languageManager.get("ui.userselectpage._01.name"));
        refreshList.setText(Launcher.languageManager.get("ui.userselectpage.account.list.refresh"));
    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
