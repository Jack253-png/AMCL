package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.AccountInfoItem;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.account.microsoft.MicrosoftLoginDialog;
import com.mcreater.amcl.pages.dialogs.account.offline.OfflineUserCreateDialog;
import com.mcreater.amcl.pages.dialogs.account.offline.OfflineUserCustomSkinDialog;
import com.mcreater.amcl.pages.dialogs.account.offline.OfflineUserModifyDialog;
import com.mcreater.amcl.pages.dialogs.commons.InputDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;
import static com.mcreater.amcl.Launcher.configReader;

public class UserSelectPage extends AbstractAnimationPage {
    VBox sideBar;
    SmoothableListView<AccountInfoItem> userList;
    AdvancedScrollPane page1;

    JFXButton menuButtonOffline;
    JFXButton menuButtonMicrosoft;
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
                        Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.STEVE.uuid, OffLineUser.SkinType.STEVE.isSlim, null, null));
                        dialog.close();
                        reloadUser();
                        break;
                    case 1:
                        Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.ALEX.uuid, OffLineUser.SkinType.ALEX.isSlim, null, null));
                        dialog.close();
                        reloadUser();
                        break;
                    case 2:
                        InputDialog dialog1 = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                        dialog1.setCancel(event12 -> dialog1.close());
                        dialog1.setEvent(event13 -> {
                            dialog1.close();
                            LoadingDialog dialog2 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.offline.id"));
                            dialog2.Create();
                            new Thread(() -> {
                                String uuid = OffLineUser.SkinType.STEVE.uuid;
                                boolean isSlim = OffLineUser.SkinType.STEVE.isSlim;
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
                        break;
                    case 3:
                        OfflineUserCustomSkinDialog dialog2 = new OfflineUserCustomSkinDialog(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"));
                        dialog2.setCancel(event14 -> dialog2.close());
                        dialog2.setEvent(event15 -> {
                            Launcher.configReader.configModel.accounts.add(new OffLineUser(dialog.getInputedName(), dialog2.getSelectedModelType() == 0 ? OffLineUser.SkinType.STEVE.uuid : OffLineUser.SkinType.ALEX.uuid, dialog2.getSelectedModelType() != 0, dialog2.getSkinPath(), dialog2.getCapePath()));
                            dialog.close();
                            dialog2.close();
                            reloadUser();
                        });
                        dialog2.Create();
                        break;
                }
            });
            dialog.Create();
        });

        menuButtonMicrosoft = new JFXButton();
        menuButtonMicrosoft.setFont(Fonts.s_f);
        menuButtonMicrosoft.setOnAction(event -> {
            MicrosoftLoginDialog dialog = new MicrosoftLoginDialog(Launcher.languageManager.get("ui.userselectpage._02.name"));
            dialog.setCancelEvent(event16 -> {
                dialog.stopLogin();
                dialog.close();
            });
            dialog.setLoginEvent(microsoftUser -> {
                configReader.configModel.accounts.add(microsoftUser);
                dialog.close();
                reloadUser();
            });
            dialog.Create();
        });

        Pane paneContainer = new Pane(userList.page);

        page1 = new AdvancedScrollPane(width / 4 * 3, height, paneContainer, false);

        sideBar = new VBox(menuButtonOffline, menuButtonMicrosoft, refreshList);
        sideBar.setId("config-menu");

        FXUtils.ControlSize.set(sideBar, width / 4, height);
        FXUtils.ControlSize.setWidth(menuButtonOffline, width / 4);
        FXUtils.ControlSize.setWidth(menuButtonMicrosoft, width / 4);
        FXUtils.ControlSize.setWidth(refreshList, width / 4);

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

                userList.setOnAction(() -> {
                    AccountInfoItem item2 = userList.selectedItem;
                    if (item2 != null) {
                        if (!item2.selector.isSelected()) {
                            item2.selector.setSelected(true);
                        }
                        userList.vecs.forEach(accountInfoItem -> {
                            if (accountInfoItem != item2) accountInfoItem.selector.setSelected(false);
                        });
                        Launcher.configReader.write();
                    }
                });

                item.selector.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) UserSelectPage.user.set(item.user);
                });

                item.setDelete(event -> {
                    Launcher.configReader.configModel.accounts.remove(item.user);
                    Launcher.configReader.write();

                    reloadUser();
                });
                item.setRefresh(event -> {
                    LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.refresh.title"));
                    dialog.show();
                    new Thread(() -> {
                        try {
                            item.user.refresh();
                            item.cutSkinStart();
                        } catch (IOException e) {
                            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.userselectpage.account.refresh.fail"));
                        }
                        finally {
                            FXUtils.Platform.runLater(dialog::close);
                        }
                    }).start();
                });
                item.setModify(event -> {
                    switch (item.user.getUserType()) {
                        case AbstractUser.OFFLINE:
                            OfflineUserModifyDialog dialog = new OfflineUserModifyDialog(item.user.toOfflineUser(), Launcher.languageManager.get("ui.userselectpage.modify.title"));
                            Runnable finalRunnable = () -> {
                                item.user.username = dialog.getInputedUserName();
                                dialog.close();
                                reloadUser();
                            };
                            dialog.setCancel(event12 -> dialog.close());
                            dialog.setAction(event13 -> {
                                switch (dialog.getSelection()) {
                                    case 0:
                                        item.user.uuid = OffLineUser.SkinType.STEVE.uuid;
                                        item.user.toOfflineUser().is_slim = OffLineUser.SkinType.STEVE.isSlim;
                                        finalRunnable.run();
                                        break;
                                    case 1:
                                        item.user.uuid = OffLineUser.SkinType.ALEX.uuid;
                                        item.user.toOfflineUser().is_slim = OffLineUser.SkinType.ALEX.isSlim;
                                        finalRunnable.run();
                                        break;
                                    case 2:
                                        LoadingDialog dialog1 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.offline.id"));
                                        InputDialog dialog2 = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                                        dialog2.setCancel(event14 -> dialog2.close());
                                        dialog2.setEvent(event15 -> {
                                            dialog1.show();
                                            new Thread(() -> {
                                                String uuid = OffLineUser.SkinType.STEVE.uuid;
                                                boolean isSlim = OffLineUser.SkinType.STEVE.isSlim;
                                                try {
                                                    uuid = MSAuth.getUserUUID(dialog2.f.getText());
                                                    isSlim = MSAuth.getUserSkin(uuid).skin.isSlim;
                                                }
                                                catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                item.user.uuid = uuid;
                                                item.user.toOfflineUser().is_slim = isSlim;
                                                FXUtils.Platform.runLater(() -> {
                                                    dialog2.close();
                                                    dialog1.close();
                                                    finalRunnable.run();
                                                });
                                            }).start();
                                        });
                                        new Thread(() -> {
                                            try {
                                                String name = MSAuth.getUserSkin(item.user.uuid).name;
                                                FXUtils.Platform.runLater(() -> dialog2.f.setText(name));
                                            } catch (Exception ignored) {

                                            } finally {
                                                FXUtils.Platform.runLater(() -> {
                                                    dialog1.close();
                                                    dialog2.show();
                                                });
                                            }
                                        }).start();
                                        dialog1.show();
                                        break;
                                    case 3:
                                        OfflineUserCustomSkinDialog dialog3 = new OfflineUserCustomSkinDialog(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"));
                                        dialog3.setSkinPath(item.user.toOfflineUser().skin);
                                        dialog3.setCapePath(item.user.toOfflineUser().cape);
                                        dialog3.group.cont.select(item.user.toOfflineUser().is_slim ? 1 : 0);
                                        dialog3.setCancel(event14 -> dialog3.close());
                                        dialog3.setEvent(event15 -> {
                                            item.user.toOfflineUser().is_slim = dialog3.getSelectedModelType() != 0;
                                            item.user.toOfflineUser().skin = dialog3.getSkinPath();
                                            item.user.toOfflineUser().cape = dialog3.getCapePath();
                                            dialog3.close();
                                            finalRunnable.run();
                                        });
                                        dialog3.Create();
                                        break;
                                }
                            });
                            dialog.showAndWait();
                            return;
                        default:
                            // TODO microsoft user modify (to be done)
                    }
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
        menuButtonMicrosoft.setText(Launcher.languageManager.get("ui.userselectpage._02.name"));
        refreshList.setText(Launcher.languageManager.get("ui.userselectpage.account.list.refresh"));
    }

    public void refreshType() {
        userList.vecs.forEach(AccountInfoItem::setType);
    }

    public void onExitPage() {

    }
}
