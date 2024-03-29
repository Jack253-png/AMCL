package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.AccountInfoItem;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.SmoothableListView;
import com.mcreater.amcl.pages.dialogs.account.microsoft.MicrosoftLoginDialog;
import com.mcreater.amcl.pages.dialogs.account.microsoft.MicrosoftModifyDialog;
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
import com.mcreater.amcl.util.builders.ThreadBuilder;
import javafx.beans.property.SimpleObjectProperty;
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
                        OffLineUser user0 = new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.STEVE.uuid, OffLineUser.SkinType.STEVE.isSlim, null, null);
                        Launcher.configReader.configModel.accounts.add(user0);
                        dialog.close();
                        userList.addItem(buildItem(user0));
                        break;
                    case 1:
                        OffLineUser user1 = new OffLineUser(dialog.getInputedName(), OffLineUser.SkinType.ALEX.uuid, OffLineUser.SkinType.ALEX.isSlim, null, null);
                        Launcher.configReader.configModel.accounts.add(user1);
                        dialog.close();
                        userList.addItem(buildItem(user1));
                        break;
                    case 2:
                        InputDialog dialog1 = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                        dialog1.setCancel(event12 -> dialog1.close());
                        dialog1.setEvent(event13 -> {
                            dialog1.close();
                            LoadingDialog dialog2 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.offline.id"));
                            dialog2.Create();
                            ThreadBuilder.createBuilder().runTarget(() -> {
                                String uuid = OffLineUser.SkinType.STEVE.uuid;
                                boolean isSlim = OffLineUser.SkinType.STEVE.isSlim;
                                try {
                                    uuid = MSAuth.getUserUUID(dialog1.f.getText());
                                    isSlim = MSAuth.getUserSkin(uuid).skin.isSlim;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                OffLineUser user2 = new OffLineUser(dialog.getInputedName(), uuid, isSlim, null, null);
                                Launcher.configReader.configModel.accounts.add(user2);
                                FXUtils.Platform.runLater(() -> {
                                    dialog2.close();
                                    dialog.close();
                                });
                                userList.addItem(buildItem(user2));
                            }).buildAndRun();
                        });
                        dialog1.Create();
                        break;
                    case 3:
                        OfflineUserCustomSkinDialog dialog2 = new OfflineUserCustomSkinDialog(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"));
                        dialog2.setCancel(event14 -> dialog2.close());
                        dialog2.setEvent(event15 -> {
                            OffLineUser user3 = new OffLineUser(dialog.getInputedName(), dialog2.getSelectedModelType() == 0 ? OffLineUser.SkinType.STEVE.uuid : OffLineUser.SkinType.ALEX.uuid, dialog2.getSelectedModelType() != 0, dialog2.getSkinPath(), dialog2.getCapePath());
                            Launcher.configReader.configModel.accounts.add(user3);
                            dialog.close();
                            dialog2.close();
                            userList.addItem(buildItem(user3));
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
                userList.addItem(buildItem(microsoftUser));
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
        userList.opacityProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
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
        userList.page.setDisable(true);
        userList.setDisable(true);
        refreshList.setDisable(true);
        ThreadBuilder.createBuilder().runTarget(() -> {
            checkActiveState();
            FXUtils.Platform.runLater(userList::clear);

            userList.setOnAction(() -> {
                AccountInfoItem item2 = userList.selectedItem;
                if (item2 != null) {
                    if (!item2.selector.isSelected()) {
                        item2.selector.setSelected(true);
                    }
                    userList.vecs.forEach(accountInfoItem -> {
                        if (accountInfoItem != item2) accountInfoItem.selector.setSelected(false);
                    });
                    configReader.write();
                }
            });
            configReader.configModel.accounts.forEach(user -> FXUtils.Platform.runLater(() -> userList.addItem(buildItem(user))));
            configReader.write();

            FXUtils.Platform.runLater(() -> {
                userList.page.setDisable(false);
                userList.setDisable(false);
                refreshList.setDisable(false);
            });
        }).buildAndRun();
    }

    public AccountInfoItem buildItem(AbstractUser user) {
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

            userList.removeItem(item);
        });
        item.setRefresh(event -> {
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.refresh.title"));
            dialog.show();
            ThreadBuilder.createBuilder().runTarget(() -> {
                try {
                    if (!item.user.validate()) item.user.refresh();
                    item.cutSkinStart();
                } catch (Exception e) {
                    SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.userselectpage.account.refresh.fail"));
                } finally {
                    FXUtils.Platform.runLater(dialog::close);
                    Launcher.configReader.write();
                }
            }).buildAndRun();
        });
        item.setModify(event -> {
            Runnable modify = () -> {
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
                                        ThreadBuilder.createBuilder().runTarget(() -> {
                                            String uuid = OffLineUser.SkinType.STEVE.uuid;
                                            boolean isSlim = OffLineUser.SkinType.STEVE.isSlim;
                                            try {
                                                uuid = MSAuth.getUserUUID(dialog2.f.getText());
                                                isSlim = MSAuth.getUserSkin(uuid).skin.isSlim;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            item.user.uuid = uuid;
                                            item.user.toOfflineUser().is_slim = isSlim;
                                            FXUtils.Platform.runLater(() -> {
                                                dialog2.close();
                                                dialog1.close();
                                                finalRunnable.run();
                                            });
                                        }).buildAndRun();
                                    });
                                    ThreadBuilder.createBuilder().runTarget(() -> {
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
                                    }).buildAndRun();
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
                        dialog.show();
                        return;
                    case AbstractUser.MICROSOFT:
                        MicrosoftModifyDialog dialog1 = new MicrosoftModifyDialog(Launcher.languageManager.get("ui.userselectpage.modify.title"), item.user.toMicrosoftUser());
                        dialog1.setFinish(event16 -> {
                            dialog1.close();
                            reloadUser();
                        });
                        dialog1.show();
                        return;
                    default:
                }
            };

            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.validate"));
            dialog.show();
            ThreadBuilder.createBuilder().runTarget(() -> {
                if (!item.user.validate()) {
                    SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.validate.fail.title"), Launcher.languageManager.get("ui.userselectpage.validate.fail.content"), "");
                    FXUtils.Platform.runLater(dialog::close);
                } else {
                    FXUtils.Platform.runLater(dialog::close);
                    FXUtils.Platform.runLater(modify);
                }
            }).buildAndRun();
        });
        item.setValidate(event -> {
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.validate"));
            dialog.show();
            ThreadBuilder.createBuilder().runTarget(() -> {
                if (!item.user.validate()) {
                    SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.validate.fail.title"), Launcher.languageManager.get("ui.userselectpage.validate.fail.content"), "");
                }
                FXUtils.Platform.runLater(dialog::close);
            }).buildAndRun();
        });
        return item;
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
