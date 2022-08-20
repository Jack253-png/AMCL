package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.config.ConfigModel;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.controls.skin.SkinView;
import com.mcreater.amcl.pages.dialogs.CustomSkinDialog;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.InputDialog;
import com.mcreater.amcl.pages.dialogs.LoadingDialog;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.pages.stages.FXBrowserPage;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
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
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class UserSelectPage extends AbstractMenuBarPage {
    ImageView view;
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
    ImageView decorator;
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;
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

        offlineSkin.cont.getSelectionModel().select(0);
        offlineLogin = new JFXButton();
        offlineLogin.setFont(Fonts.t_f);
        offlineLogin.setOnAction(event -> {
            Launcher.configReader.configModel.last_userType = "OFFLINE";
            Launcher.configReader.configModel.last_name = nameItem.cont.getText();

            Runnable finalRunnable = () -> {
                Launcher.configReader.configModel.last_refreshToken = "";
                Launcher.configReader.configModel.last_accessToken = "";
                Launcher.configReader.write();
                user_object.set(new OffLineUser(
                        Launcher.configReader.configModel.last_name,
                        Launcher.configReader.configModel.last_uuid,
                        Launcher.configReader.configModel.last_is_slim,
                        Launcher.configReader.configModel.last_skin_path,
                        Launcher.configReader.configModel.last_cape_path));
                FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.content"), "");
                refreshSkin();
                setP1(2);
            };

            if (offlineSkin.cont.getSelectionModel().getSelectedIndex() == 0) {
                Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                Launcher.configReader.configModel.last_is_slim = false;
                Launcher.configReader.configModel.last_skin_path = null;
                Launcher.configReader.configModel.last_cape_path = null;
                finalRunnable.run();
            }
            else if (offlineSkin.cont.getSelectionModel().getSelectedIndex() == 1) {
                Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                Launcher.configReader.configModel.last_is_slim = false;
                Launcher.configReader.configModel.last_skin_path = null;
                Launcher.configReader.configModel.last_cape_path = null;
                finalRunnable.run();
            }
            else if (offlineSkin.cont.getSelectionModel().getSelectedIndex() == 2){
                InputDialog dialog = new InputDialog(Launcher.languageManager.get("ui.userselectpage.skin.input"));
                dialog.Create();
                dialog.setEvent(event1 -> {
                    dialog.close();
                    LoadingDialog dialog1 = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.logging"));
                    dialog1.Create();
                    new Thread(() -> {
                        Launcher.configReader.configModel.last_is_slim = false;
                        Launcher.configReader.configModel.last_skin_path = null;
                        Launcher.configReader.configModel.last_cape_path = null;
                        try {
                            Launcher.configReader.configModel.last_uuid = MSAuth.getUserUUID(dialog.f.getText());
                        }
                        catch (Exception e){
                            Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
                        }
                        Platform.runLater(dialog1::close);
                        Platform.runLater(finalRunnable);
                    }).start();
                });
            }
            else if (offlineSkin.cont.getSelectionModel().getSelectedIndex() == 3) {
                Launcher.configReader.configModel.last_uuid = OffLineUser.ALEX;
                CustomSkinDialog dialog = new CustomSkinDialog(Launcher.languageManager.get("ui.userselectpage.custom.title"));

                dialog.setEvent(event12 -> {
                    switch (dialog.changeModelSelect.cont.getSelectionModel().getSelectedIndex()){
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
                    Launcher.configReader.configModel.last_skin_path = dialog.skin;
                    Launcher.configReader.configModel.last_cape_path = dialog.cape;
                    Launcher.configReader.write();
                    Platform.runLater(finalRunnable);
                    Platform.runLater(dialog::close);
                });
                dialog.Create();
            }
        });
        switch (ConfigModel.UserType.valueOf(Launcher.configReader.configModel.last_userType)){
            case OFFLINE:
                if (Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null){
                    nameItem.cont.setText(Launcher.configReader.configModel.last_name);
                    user_object.set(new OffLineUser(
                            Launcher.configReader.configModel.last_name,
                            Launcher.configReader.configModel.last_uuid,
                            Launcher.configReader.configModel.last_is_slim,
                            Launcher.configReader.configModel.last_skin_path,
                            Launcher.configReader.configModel.last_cape_path));
                }
                break;
            case MICROSOFT:
                if (Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null || Launcher.configReader.configModel.last_accessToken != null || Launcher.configReader.configModel.last_refreshToken != null){
                    user_object.set(new MicrosoftUser(
                            Launcher.configReader.configModel.last_accessToken,
                            Launcher.configReader.configModel.last_name,
                            Launcher.configReader.configModel.last_uuid,
                            new Vector<>(),
                            Launcher.configReader.configModel.last_refreshToken));
                }
                break;
        }
        offL.getChildren().addAll(nameItem, offlineSkin, offlineLogin);
        msLogin = new JFXButton();
        msLogin.setFont(Fonts.s_f);
        msLogin.setOnAction(event -> {
            FXBrowserPage p = new FXBrowserPage(MSAuth.loginUrl);
            msLogin.setDisable(true);
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.logging"));
            dialog.Create();
            new Thread(() -> {
                while (p.user == null && p.ex == null) {
                    Sleeper.sleep(1000);
                }
                if (p.user != null){
                    Launcher.configReader.configModel.last_uuid = p.user.uuid;
                    Launcher.configReader.configModel.last_name = p.user.username;
                    Launcher.configReader.configModel.last_accessToken = p.user.accessToken;
                    Launcher.configReader.configModel.last_refreshToken = p.user.refreshToken;
                    Launcher.configReader.configModel.last_userType = "MICROSOFT";
                    Launcher.configReader.configModel.last_is_slim = false;
                    Launcher.configReader.configModel.last_skin_path = null;
                    Launcher.configReader.configModel.last_cape_path = null;
                    user_object.set(p.user);
                    Launcher.configReader.write();
                    Platform.runLater(() -> {
                        dialog.close();
                        FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.ms.content"), "");
                        msLogin.setDisable(false);
                        refreshSkin();
                        setP1(2);
                    });
                }
                else {
                    Platform.runLater(() -> {
                        dialog.close();
                        FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.login.failed"), p.ex.toString(), "");
                        msLogin.setDisable(false);
                    });
                }
            }).start();
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

        JFXButton logout = new JFXButton();
        JFXButton refresh = new JFXButton();
        logout.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding(this::returnBlack), 40 ,40));
        refresh.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(this::returnBlack), 40 ,40));
        FXUtils.ControlSize.setAll(40, 40, logout, refresh);

        refresh.setOnAction(event -> {
            refresh.setDisable(true);
            LoadingDialog dialog = new LoadingDialog(Launcher.languageManager.get("ui.userselectpage.account.refresh.title"));
            dialog.Create();
            new Thread(() -> {
                try {
                    user_object.get().refresh();
                    Launcher.configReader.configModel.last_uuid = user_object.get().uuid;
                    Launcher.configReader.configModel.last_name = user_object.get().username;
                    if (user_object.get() instanceof MicrosoftUser) {
                        Launcher.configReader.configModel.last_accessToken = user_object.get().accessToken;
                        Launcher.configReader.configModel.last_refreshToken = ((MicrosoftUser) user_object.get()).refreshToken;
                    }
                    Launcher.configReader.write();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.account.refresh.fail"), e.toString(), ""));
                }
                Platform.runLater(() -> refresh.setDisable(false));
                Platform.runLater(dialog::close);
                Platform.runLater(this::refreshSkin);
            }).start();
        });

        logout.setOnAction(event -> {
            user_object.set(null);
            setP1(0);
            Launcher.configReader.configModel.last_uuid = null;
            Launcher.configReader.configModel.last_name = null;
            Launcher.configReader.configModel.last_accessToken = null;
            Launcher.configReader.configModel.last_refreshToken = null;
            Launcher.configReader.configModel.last_userType = "OFFLINE";
            Launcher.configReader.configModel.last_is_slim = false;
            Launcher.configReader.configModel.last_skin_path = null;
            Launcher.configReader.configModel.last_cape_path = null;
            Launcher.configReader.write();
        });

        Pane p = new Pane();
        p.getChildren().addAll(view, decorator);

        HBox g = new HBox(p, name, refresh, logout);
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
                FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.not_logged.title"), Launcher.languageManager.get("ui.userselectpage.not_logged.content"), "");
            }
        });
        this.addNewPair(new Pair<>(b1, p1));
        this.addNewPair(new Pair<>(b2, p2));
        this.addNewPair(new Pair<>(profile, p3));
        super.setP1(0);
        super.setButtonType(JFXButton.ButtonType.RAISED);
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
        OffLineUser user = null;
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                dialog.close();
                name.setText(String.format(Launcher.languageManager.get("ui.userselectpage.hello"), user_object.get().username));
                setP1(2);
            });
        }).start();
    }
    public void setImage(Image i, boolean isSlim){
        WritableImage image = new WritableImage(8, 8);
        WritableImage dec = new WritableImage(8, 8);
        try {
            for (int x = 8; x < 16; x++) {
                for (int y = 8; y < 16; y++) {
                    image.getPixelWriter().setArgb(x - 8, y - 8, i.getPixelReader().getArgb(x, y));
                }
            }
            if (i.getWidth() >= 64 && i.getHeight() >= 64) {
                for (int x = 40; x < 48; x++) {
                    for (int y = 8; y < 16; y++) {
                        dec.getPixelWriter().setArgb(x - 40, y - 8, i.getPixelReader().getArgb(x, y));
                    }
                }
            }
        }
        catch (Exception ignored){
            ignored.printStackTrace();
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
