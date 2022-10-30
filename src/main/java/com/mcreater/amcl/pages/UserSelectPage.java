package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.api.auth.users.UserHashManager;
import com.mcreater.amcl.config.ConfigModel;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.controls.skin.SkinView;
import com.mcreater.amcl.pages.dialogs.commons.ContinueDialog;
import com.mcreater.amcl.pages.dialogs.commons.InputDialog;
import com.mcreater.amcl.pages.dialogs.commons.LoadingDialog;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.dialogs.skin.MicrosoftSkinManageDialog;
import com.mcreater.amcl.pages.dialogs.skin.OfflineEditAccountContentDialog;
import com.mcreater.amcl.pages.dialogs.skin.OfflineSkinManageDialog;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.stages.browser.AbstractWebBrowser;
import com.mcreater.amcl.pages.stages.browser.ChroumiumWebBrowser;
import com.mcreater.amcl.pages.stages.browser.WebkitWebBrowser;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.SimpleFunctions;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class UserSelectPage extends AbstractMenuBarPage {
    public ImageView view;
    SettingPage p1;
    JFXButton b1;
    VBox offL;
    StringItem nameItem;
    JFXButton offlineLogin;
    SettingPage p2;
    JFXButton b2;
    JFXButton msLogin;
    public static ObjectProperty<AbstractUser> user_object;
    JFXButton profile;
    SettingPage p3;
    SkinView skin3d;
    Label name;

    ListItem<Label> offlineSkin;
    Label onlineUser;
    Label custom;
    public ImageView decorator;
    public String handleNullString(String raw){
        if (raw == null) return "";
        else return raw;
    }
    public Pane p;
    public JFXButton edit;
    public JFXButton refresh;
    public JFXButton logout;
    public JFXButton refreshSkin;
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;

        SimpleFunctions.Arg0Func<Integer> getUserTypeIndex = () -> {
            if (user_object.get() != null){
                if (user_object.get() instanceof OffLineUser){
                    OffLineUser temp_user = (OffLineUser) user_object.get();
                    if (temp_user.capeUseable() || temp_user.skinUseable() || temp_user.elytraUseable()) {
                        return 3;
                    }
                    else {
                        switch (user_object.get().uuid) {
                            case OffLineUser.STEVE:
                                return 0;
                            case OffLineUser.ALEX:
                                return 1;
                            default:
                                return 2;
                        }
                    }
                }
            }
            return 0;
        };

        SimpleFunctions.Arg1Func<Boolean, Integer> castIntToBoolean = arg1 -> arg1 ? 1 : 0;

        user_object = new SimpleObjectProperty<>();
        mainBox = new VBox();
        offL = new VBox();
        nameItem = new StringItem("", width / 4 * 3);
        offlineSkin = new ListItem<>("", width / 4 * 3);
        onlineUser = new Label();
        onlineUser.setFont(Fonts.t_f);
        custom = new Label();
        custom.setFont(Fonts.t_f);
        for (String s : J8Utils.createList("Steve", "Alex")){
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            offlineSkin.cont.getItems().add(l);
        }
        offlineSkin.cont.getItems().addAll(onlineUser, custom);
        offlineSkin.cont.getSelectionModel().select(getUserTypeIndex.run());

        logout = new JFXButton();
        refresh = new JFXButton();
        edit = new JFXButton();
        refreshSkin = new JFXButton();

        logout.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding(this::returnBlack), 40 ,40));
        refresh.setGraphic(Launcher.getSVGManager().check(Bindings.createObjectBinding(this::returnBlack), 40 ,40));
        edit.setGraphic(Launcher.getSVGManager().accountEdit(Bindings.createObjectBinding(this::returnBlack), 40 ,40));
        refreshSkin.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(this::returnBlack), 40, 40));
        FXUtils.ControlSize.setAll(40, 40, logout, refresh, edit, refreshSkin);

        refreshSkin.setOnAction(event -> refreshSkin());

        refresh.setOnAction(event -> {
            refresh.setDisable(true);
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.refresh.title")) {
                public void outAnimation() {

                }
            };
            dialog.Create();
            new Thread(() -> {
                Runnable run = () -> {
                    Platform.runLater(() -> refresh.setDisable(false));
                    Platform.runLater(dialog::close);
                    Platform.runLater(this::refreshSkin);
                    UserHashManager.writeSafe(user_object.get());
                };
                Runnable ref = () -> {
                    try {
                        user_object.get().refresh();
                        Launcher.configReader.configModel.last_uuid = user_object.get().uuid;
                        Launcher.configReader.configModel.last_name = user_object.get().username;
                        if (user_object.get() instanceof MicrosoftUser) {
                            Launcher.configReader.configModel.last_accessToken = user_object.get().accessToken;
                            Launcher.configReader.configModel.last_refreshToken = user_object.get().refreshToken;
                        }
                        Launcher.configReader.write();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.account.refresh.fail"), e.toString(), ""));
                    }
                    finally {
                        run.run();
                    }
                };
                if (!user_object.get().vaildate()) {
                    ref.run();
                }
                else {
                    Platform.runLater(() -> {
                        ContinueDialog dialog1 = new ContinueDialog(Launcher.languageManager.get("ui.userselectpage.user_vaildated.title"), Launcher.languageManager.get("ui.userselectpage.user_vaildated.content"));
                        dialog1.Create();
                        dialog1.setEvent(event18 -> {dialog1.close();run.run();});
                        dialog1.setCancel(event19 -> new Thread(() -> {
                            Platform.runLater(dialog1::close);
                            ref.run();
                        }).start());
                    });
                }

            }).start();
        });

        logout.setOnAction(event -> {
            user_object.set(null);
            UserHashManager.clearUserData();
            setP1(0);
        });



        user_object.addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                if (newValue instanceof OffLineUser) {
                    edit.setOnAction(event -> {
                        OfflineEditAccountContentDialog dialog = new OfflineEditAccountContentDialog(Launcher.languageManager.get("ui.userselectpage.account.edit")) {
                            public void outAnimation() {

                            }
                        };
                        OffLineUser temp_user = (OffLineUser) user_object.get();
                        dialog.item2.cont.getSelectionModel().select(getUserTypeIndex.run());
                        dialog.item.cont.setText(temp_user.username);
                        Runnable finalRunnable = () -> {
                            Launcher.configReader.write();
                            user_object.set(new OffLineUser(
                                    Launcher.configReader.configModel.last_name,
                                    Launcher.configReader.configModel.last_uuid,
                                    Launcher.configReader.configModel.last_is_slim,
                                    Launcher.configReader.configModel.last_skin_path,
                                    Launcher.configReader.configModel.last_cape_path,
                                    Launcher.configReader.configModel.last_elytra_path
                            ));
                            refreshSkin();
                            dialog.close();
                        };
                        dialog.setCancel(event17 -> dialog.close());
                        dialog.setEvent(event13 -> {
                            Launcher.configReader.configModel.last_name = dialog.item.cont.getText();
                            switch (dialog.item2.cont.getSelectionModel().getSelectedIndex()){
                                case 0:
                                    Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                                    Launcher.configReader.configModel.last_is_slim = false;
                                    Launcher.configReader.configModel.last_skin_path = null;
                                    Launcher.configReader.configModel.last_cape_path = null;
                                    Launcher.configReader.configModel.last_elytra_path = null;
                                    finalRunnable.run();
                                    break;
                                case 1:
                                    Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                                    Launcher.configReader.configModel.last_is_slim = true;
                                    Launcher.configReader.configModel.last_skin_path = null;
                                    Launcher.configReader.configModel.last_cape_path = null;
                                    Launcher.configReader.configModel.last_elytra_path = null;
                                    finalRunnable.run();
                                    break;
                                case 2:
                                    InputDialog d1 = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                                    LoadingDialog d3 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.loading"));
                                    d3.Create();
                                    new Thread(() -> {
                                        try {
                                            d1.f.setText(MSAuth.getUserSkin(Launcher.configReader.configModel.last_uuid).name);
                                        } catch (Exception ignored) {}
                                        Platform.runLater(d3::close);
                                        Platform.runLater(() -> {
                                            d1.Create();
                                            d1.setCancel(event16 -> d1.close());
                                            d1.setEvent(event1 -> {
                                                d1.close();
                                                LoadingDialog dialog1 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.updating"));
                                                dialog1.Create();
                                                new Thread(() -> {
                                                    Launcher.configReader.configModel.last_skin_path = null;
                                                    Launcher.configReader.configModel.last_cape_path = null;
                                                    Launcher.configReader.configModel.last_elytra_path = null;
                                                    try {
                                                        Launcher.configReader.configModel.last_uuid = MSAuth.getUserUUID(d1.f.getText());
                                                    }
                                                    catch (Exception e){
                                                        Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                                                    }
                                                    try {
                                                        Launcher.configReader.configModel.last_is_slim = MSAuth.getUserSkinFromName(d1.f.getText()).skins.get(0).isSlim;
                                                    }
                                                    catch (Exception e){
                                                        Launcher.configReader.configModel.last_is_slim = false;
                                                    }
                                                    Platform.runLater(dialog1::close);
                                                    Platform.runLater(finalRunnable);
                                                }).start();
                                            });
                                        });
                                    }).start();
                                    break;
                                case 3:
                                    Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                                    OfflineSkinManageDialog d2 = new OfflineSkinManageDialog(Launcher.languageManager.get("ui.userselectpage.custom.title"));
                                    d2.changeModelSelect.cont.getSelectionModel().select(castIntToBoolean.run(Launcher.configReader.configModel.last_is_slim));
                                    d2.skin = handleNullString(Launcher.configReader.configModel.last_skin_path);
                                    d2.skin_ui.cont.setText(handleNullString(Launcher.configReader.configModel.last_skin_path));
                                    d2.cape = handleNullString(Launcher.configReader.configModel.last_cape_path);
                                    d2.cape_ui.cont.setText(handleNullString(Launcher.configReader.configModel.last_cape_path));
                                    d2.elytra = handleNullString(Launcher.configReader.configModel.last_elytra_path);
                                    d2.elytra_ui.cont.setText(handleNullString(Launcher.configReader.configModel.last_elytra_path));

                                    d2.setCancel(event1 -> d2.close());
                                    d2.setEvent(event12 -> {
                                        switch (d2.changeModelSelect.cont.getSelectionModel().getSelectedIndex()){
                                            default:
                                            case 0:
                                                Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                                                Launcher.configReader.configModel.last_is_slim = false;
                                                break;
                                            case 1:
                                                Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                                                Launcher.configReader.configModel.last_is_slim = true;
                                                break;
                                        }
                                        Launcher.configReader.configModel.last_skin_path = d2.skin;
                                        Launcher.configReader.configModel.last_cape_path = d2.cape;
                                        Launcher.configReader.configModel.last_elytra_path = d2.elytra;
                                        Launcher.configReader.write();
                                        Platform.runLater(d2::close);
                                        finalRunnable.run();
                                    });
                                    d2.Create();
                                    break;
                            }
                        });
                        dialog.Create();
                    });
                }
                else if (newValue instanceof MicrosoftUser) {
                    edit.setOnAction(event -> {
                        MicrosoftUser user = (MicrosoftUser) newValue;
                        LoadingDialog di = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.loading"));
                        di.show();
                        new Thread(() -> {
                            if (user.vaildate()) {
                                Platform.runLater(() -> {
                                    di.close();
                                    MicrosoftSkinManageDialog dialog = new MicrosoftSkinManageDialog(Launcher.languageManager.get("ui.userselectpage.custom.title"), user) {
                                        public void outAnimation() {

                                        }
                                    };
                                    try {
                                        dialog.loadCapes();
                                    }
                                    catch (Exception ignored){

                                    }
                                    dialog.show();
                                    dialog.addButton.setOnAction(event111 -> {
                                        dialog.close();
                                        refreshSkin();
                                    });
                                });
                            }
                            else {
                                Platform.runLater(() -> {
                                    di.close();
                                    SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.user_unvaildated.title"), Launcher.languageManager.get("ui.userselectpage.user_unvaildated.content"), "");
                                });
                            }
                        }).start();
                    });
                }
                else {
                    edit.setOnAction(null);
                }
            }
            UserHashManager.writeSafe(newValue);
        });

        offlineSkin.cont.getSelectionModel().select(0);
        offlineLogin = new JFXButton();
        offlineLogin.setFont(Fonts.t_f);
        offlineLogin.setOnAction(event -> {
            Runnable finalRunnable = () -> {
                Launcher.configReader.configModel.last_userType = "OFFLINE";
                Launcher.configReader.configModel.last_name = nameItem.cont.getText();
                Launcher.configReader.configModel.last_refreshToken = "";
                Launcher.configReader.configModel.last_accessToken = "";
                Launcher.configReader.write();
                user_object.set(new OffLineUser(
                        Launcher.configReader.configModel.last_name,
                        Launcher.configReader.configModel.last_uuid,
                        Launcher.configReader.configModel.last_is_slim,
                        Launcher.configReader.configModel.last_skin_path,
                        Launcher.configReader.configModel.last_cape_path,
                        Launcher.configReader.configModel.last_elytra_path
                ));
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.content"), "");
                refreshSkin();
                setP1(2);
            };

            switch (offlineSkin.cont.getSelectionModel().getSelectedIndex()) {
                case 0:
                    Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                    Launcher.configReader.configModel.last_is_slim = false;
                    Launcher.configReader.configModel.last_skin_path = null;
                    Launcher.configReader.configModel.last_cape_path = null;
                    Launcher.configReader.configModel.last_elytra_path = null;
                    finalRunnable.run();
                    break;
                case 1:
                    Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                    Launcher.configReader.configModel.last_is_slim = true;
                    Launcher.configReader.configModel.last_skin_path = null;
                    Launcher.configReader.configModel.last_cape_path = null;
                    Launcher.configReader.configModel.last_elytra_path = null;
                    finalRunnable.run();
                    break;
                case 2:
                    InputDialog dialog = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                    dialog.Create();
                    dialog.setCancel(event14 -> dialog.close());
                    dialog.setEvent(event1 -> {
                        dialog.close();
                        LoadingDialog dialog1 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.logging"));
                        dialog1.Create();
                        new Thread(() -> {
                            Launcher.configReader.configModel.last_skin_path = null;
                            Launcher.configReader.configModel.last_cape_path = null;
                            Launcher.configReader.configModel.last_elytra_path = null;
                            try {
                                Launcher.configReader.configModel.last_uuid = MSAuth.getUserUUID(dialog.f.getText());
                            } catch (Exception e) {
                                Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                            }
                            try {
                                Launcher.configReader.configModel.last_is_slim = MSAuth.getUserSkinFromName(dialog.f.getText()).skins.get(0).isSlim;
                            } catch (Exception e) {
                                Launcher.configReader.configModel.last_is_slim = false;
                            }
                            Platform.runLater(dialog1::close);
                            Platform.runLater(finalRunnable);
                        }).start();
                    });
                    break;
                case 3:
                    Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                    OfflineSkinManageDialog d2 = new OfflineSkinManageDialog(Launcher.languageManager.get("ui.userselectpage.custom.title"));
                    d2.setCancel(event15 -> d2.close());
                    d2.setEvent(event12 -> {
                        switch (d2.changeModelSelect.cont.getSelectionModel().getSelectedIndex()) {
                            default:
                            case 0:
                                Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                                Launcher.configReader.configModel.last_is_slim = false;
                                break;
                            case 1:
                                Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                                Launcher.configReader.configModel.last_is_slim = true;
                                break;
                        }
                        Launcher.configReader.configModel.last_skin_path = d2.skin;
                        Launcher.configReader.configModel.last_cape_path = d2.cape;
                        Launcher.configReader.configModel.last_elytra_path = d2.elytra;
                        Launcher.configReader.write();
                        Platform.runLater(finalRunnable);
                        Platform.runLater(d2::close);
                    });
                    d2.Create();
                    break;
                }
            });
        switch (ConfigModel.UserType.valueOf(Launcher.configReader.configModel.last_userType)){
            case OFFLINE:
                if ((Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null)){
                    nameItem.cont.setText(Launcher.configReader.configModel.last_name);
                    user_object.set(new OffLineUser(
                            Launcher.configReader.configModel.last_name,
                            Launcher.configReader.configModel.last_uuid,
                            Launcher.configReader.configModel.last_is_slim,
                            Launcher.configReader.configModel.last_skin_path,
                            Launcher.configReader.configModel.last_cape_path,
                            Launcher.configReader.configModel.last_elytra_path
                    ));
                }
                if (!UserHashManager.vaildateSafe(user_object.get())){
                    user_object.set(null);
                    UserHashManager.clearUserData();
                }
                break;
            case MICROSOFT:
                if ((Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null || Launcher.configReader.configModel.last_accessToken != null || Launcher.configReader.configModel.last_refreshToken != null)){
                    user_object.set(new MicrosoftUser(
                            Launcher.configReader.configModel.last_accessToken,
                            Launcher.configReader.configModel.last_name,
                            Launcher.configReader.configModel.last_uuid,
                            new Vector<>(),
                            Launcher.configReader.configModel.last_refreshToken));
                }
                if (!UserHashManager.vaildateSafe(user_object.get())){
                    user_object.set(null);
                    UserHashManager.clearUserData();
                }
                break;
        }
        offL.getChildren().addAll(nameItem, offlineSkin, offlineLogin);
        msLogin = new JFXButton();
        msLogin.setFont(Fonts.s_f);
        msLogin.setOnAction(event -> {
            msLogin.setDisable(true);
            ProcessDialog dialog = new ProcessDialog(1, Launcher.languageManager.get("ui.userselectpage.logging")) {
                public void outAnimation() {

                }
            };
            dialog.setV(0, 0, Launcher.languageManager.get("ui.msauth._01"));


            AbstractWebBrowser p;
            try {
                p = AbstractWebBrowser.getBrowserImpl(MSAuth.LOGIN_URL, Launcher.configReader.configModel.use_chuoumium_core ? AbstractWebBrowser.BrowserType.CHROUMIUM : AbstractWebBrowser.BrowserType.WEBKIT);
            }
            catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    dialog.close();
                    SimpleDialogCreater.exception(e);
                    msLogin.setDisable(false);
                });
                return;
            }
            p.setDialog(dialog);
            p.open();



            Launcher.stage.hide();

            Thread t = new Thread(() -> {
                while (p.user == null && p.ex == null) {
                    Sleeper.sleep(100);
                }
                if (p.user != null){
                    Launcher.configReader.configModel.last_uuid = p.user.uuid;
                    Launcher.configReader.configModel.last_name = p.user.username;
                    Launcher.configReader.configModel.last_accessToken = p.user.accessToken;
                    Launcher.configReader.configModel.last_refreshToken = p.user.refreshToken;
                    Launcher.configReader.configModel.last_userType = "MICROSOFT";

                    try {
                        Launcher.configReader.configModel.last_is_slim = MSAuth.getUserSkin(p.user.uuid).skins.get(0).isSlim;
                    } catch (Exception e) {
                        Launcher.configReader.configModel.last_is_slim = false;
                    }
                    Launcher.configReader.configModel.last_skin_path = null;
                    Launcher.configReader.configModel.last_cape_path = null;
                    Launcher.configReader.configModel.last_elytra_path = null;
                    user_object.set(p.user);
                    Launcher.configReader.write();
                    Platform.runLater(() -> {
                        dialog.close();
                        SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.ms.content"), "");
                        msLogin.setDisable(false);
                        refreshSkin();
                        setP1(2);
                    });
                }
                else {
                    Platform.runLater(() -> {
                        dialog.close();
                        SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.login.failed"), p.ex.toString(), "");
                        msLogin.setDisable(false);
                    });
                }
            });
            JFXButton cancel = new JFXButton(Launcher.languageManager.get("ui.userselectpage.cancel"));
            ThemeManager.loadButtonAnimates(cancel);
            dialog.layout.getActions().add(cancel);
            cancel.setOnAction(event110 -> {
                dialog.close();
                p.loginThread.stop();
                t.stop();
                msLogin.setDisable(false);
            });
            p.setOnHiding(event1 -> {
                dialog.Create();
                Launcher.stage.show();
            });
            p.setOnCloseRequest(event1 -> {
                dialog.close();
                t.stop();
                msLogin.setDisable(false);
                Launcher.stage.show();
                p.loginThread.stop();
            });
            t.start();
        });

        view = new ImageView();
        decorator = new ImageView();
        p1 = new SettingPage(width / 4 * 3, height, offL);
        b1 = new JFXButton();
        b1.setFont(Fonts.s_f);
        p2 = new SettingPage(width / 4 * 3, height, new VBox(msLogin));
        b2 = new JFXButton();
        b2.setFont(Fonts.s_f);
        profile = new JFXButton();
        profile.setFont(Fonts.s_f);

        skin3d = new SkinView(width / 4 * 3, height - 50 - Launcher.barSize);
        skin3d.enableRotation(.5);

        name = new Label();
        name.setFont(Fonts.s_f);

        p = new Pane();
        p.getChildren().addAll(view, decorator);

        HBox g = new HBox(p, name, refreshSkin, refresh, logout, edit);
        g.setSpacing(15);
        FXUtils.ControlSize.setHeight(g, 50);
        p3 = new SettingPage(width / 4 * 3, height, new VBox(g, skin3d));
        FXUtils.ControlSize.setWidth(b1, this.width / 4);
        FXUtils.ControlSize.setWidth(b2, this.width / 4);
        FXUtils.ControlSize.setWidth(profile, this.width / 4);

        b1.setOnAction(event -> {if (user_object.get() == null) setP1(0);});
        b2.setOnAction(event -> {if (user_object.get() == null) setP1(1);});

        profile.setOnAction(event -> {
            if (user_object.get() != null) {
                refreshSkin();
            }
            else {
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.userselectpage.not_logged.title"), Launcher.languageManager.get("ui.userselectpage.not_logged.content"), "");
            }
        });
        this.addNewPair(new ImmutablePair<>(b1, p1));
        this.addNewPair(new ImmutablePair<>(b2, p2));
        this.addNewPair(new ImmutablePair<>(profile, p3));
        super.setP1(0);
        super.setButtonType(JFXButton.ButtonType.RAISED);
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
    public Image getOfflineUserSkin(OffLineUser user) {
        try {
            if (user.skinUseable()) {
                return new Image(Files.newInputStream(Paths.get(user.skin)));
            } else {
                if (user.is_slim) {
                    return SkinView.ALEX;
                } else {
                    return SkinView.STEVE;
                }
            }
        }
        catch (Exception e){
            if (user.is_slim) {
                return SkinView.ALEX;
            } else {
                return SkinView.STEVE;
            }
        }
    }
    public Image getOfflineUserCape(OffLineUser user) {
        try {
            if (user.capeUseable()) {
                return new Image(Files.newInputStream(Paths.get(user.cape)));
            } else {
                return new WritableImage(1, 1);
            }
        }
        catch (IOException ignored){
            return new WritableImage(1, 1);
        }
    }
    public Image getOnlineUserSkin(MSAuth.McProfileModel.McSkinModel model){
        if (model.url.equals("https://")){
            if (model.isSlim){
                return SkinView.ALEX;
            }
            else {
                return SkinView.STEVE;
            }
        }
        else {
            return new Image(model.url);
        }
    }
    public Image getOnlineUserCape(MSAuth.McProfileModel.McSkinModel model){
        if (!model.cape.equals("https://")){
            return new Image(model.cape);
        }
        else {
            return new WritableImage(1, 1);
        }
    }
    public void refreshSkin(){
        CountDownLatch latch = new CountDownLatch(1);
        OffLineUser user;
        Image skin;
        Image cape;
        if (user_object.get() instanceof OffLineUser){
            switch (user_object.get().uuid){
                case OffLineUser.STEVE:
                    user = (OffLineUser) user_object.get();
                    skin = getOfflineUserSkin(user);
                    cape = getOfflineUserCape(user);
                    try {
                        skin3d.updateSkin(skin, false, cape);
                    }
                    catch (IllegalArgumentException e){
                        skin3d.updateSkin(skin, false, new WritableImage(1, 1));
                    }
                    latch.countDown();
                    setImage(skin, false);
                    break;
                case OffLineUser.ALEX:
                    user = (OffLineUser) user_object.get();
                    skin = getOfflineUserSkin(user);
                    cape = getOfflineUserCape(user);
                    try {
                        skin3d.updateSkin(skin, true, cape);
                    }
                    catch (IllegalArgumentException e){
                        skin3d.updateSkin(skin, true, new WritableImage(1, 1));
                    }
                    latch.countDown();
                    setImage(skin, true);
                    break;
                default:
                    new Thread(() -> {
                        try {
                            for (MSAuth.McProfileModel.McSkinModel model : MSAuth.getUserSkin(user_object.get().uuid).skins) {
                                Image skinImage = getOnlineUserSkin(model);
                                Image capeImage = getOnlineUserCape(model);
                                Platform.runLater(() -> {
                                    skin3d.updateSkin(skinImage, model.isSlim, capeImage);
                                    setImage(skinImage, model.isSlim);
                                });
                                break;
                            }
                        } catch (Exception e) {
                            Platform.runLater(() -> setImage(SkinView.STEVE, false));
                        }
                        latch.countDown();
                    }).start();
                    break;
            }
        }
        else if (user_object.get() instanceof MicrosoftUser){
            new Thread(() -> {
                try {
                    for (MSAuth.McProfileModel.McSkinModel model : MSAuth.getUserSkin(user_object.get().uuid).skins) {
                        Image skinImage = getOnlineUserSkin(model);
                        Image capeImage = getOnlineUserCape(model);
                        Platform.runLater(() -> {
                            skin3d.updateSkin(skinImage, model.isSlim, capeImage);
                            setImage(skinImage, model.isSlim);
                        });
                        break;
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> setImage(SkinView.STEVE, false));
                }
                latch.countDown();
            }).start();
        }
        LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.skin.loading.title"));
        dialog.Create();
        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException ignored) {

            }
            Platform.runLater(() -> {
                dialog.close();
                name.setText(String.format(Launcher.languageManager.get("ui.userselectpage.hello"), user_object.get().username));
                setP1(2);
            });
        }).start();
    }
    public void setImage(Image i, boolean isSlim){
        int base1 = 40;

        int siz = (int) (i.getWidth() / 8);
        WritableImage image = new WritableImage(siz, siz);
        WritableImage dec = new WritableImage(siz, siz);
        try {
            for (int x = siz; x < siz * 2; x++) {
                for (int y = siz; y < siz * 2; y++) {
                    image.getPixelWriter().setArgb(x - siz, y - siz, i.getPixelReader().getArgb(x, y));
                }
            }
            if (i.getWidth() >= 64 && i.getHeight() >= 64) {
                for (int x = base1; x < base1 + siz; x++) {
                    for (int y = siz; y < siz * 2; y++) {
                        dec.getPixelWriter().setArgb(x - base1, y - siz, i.getPixelReader().getArgb(x, y));
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            if (isSlim){
                setImage(SkinView.ALEX, true);
            }
            else {
                setImage(SkinView.STEVE, false);
            }
        }

        view.setImage(scrollImage(image, 6, 6));
        view.setFitHeight(view.getImage().getHeight());
        view.setFitWidth(view.getImage().getWidth());
        decorator.setImage(scrollImage(dec, 6, 6));
        decorator.setFitHeight(view.getImage().getHeight());
        decorator.setFitWidth(view.getImage().getWidth());
    }
    public Image scrollImage(Image image, int xadd, int yadd){
        WritableImage image1 = new WritableImage((int) (image.getWidth() * xadd), (int) (image.getHeight() * yadd));
        for (int x = 0;x < image.getWidth();x++){
            for (int y = 0;y < image.getHeight();y++){
                for (int ax = 0;ax < xadd;ax++){
                    for (int ay = 0;ay < yadd;ay++){
                        image1.getPixelWriter().setArgb(x * xadd + ax, y * yadd + ay, image.getPixelReader().getArgb(x, y));
                    }
                }
            }
        }
        return image1;
    }

    public void refresh() {
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
        new Thread(() -> {
            Sleeper.sleep(200);
            Platform.runLater(() -> {
                if (user_object.get() != null){
                    refreshSkin();
                }
            });
        }).start();
    }

    public void refreshLanguage() {
        b1.setText(Launcher.languageManager.get("ui.userselectpage._01.name"));
        nameItem.title.setText(Launcher.languageManager.get("ui.userselectpage.nameItem"));
        offlineLogin.setText(Launcher.languageManager.get("ui.userselectpage.login.name"));
        b2.setText(Launcher.languageManager.get("ui.userselectpage._02.name"));
        msLogin.setText(Launcher.languageManager.get("ui.userselectpage.login.name"));
        profile.setText(Launcher.languageManager.get("ui.userselectpage._03.name"));
        offlineSkin.name.setText(Launcher.languageManager.get("ui.userselectpage.login.offlineskin"));
        onlineUser.setText(Launcher.languageManager.get("ui.userselectpage.skin.online"));
        custom.setText(Launcher.languageManager.get("ui.userselectpage.skin.custom"));
    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
