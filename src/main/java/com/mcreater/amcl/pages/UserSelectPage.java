package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.config.ConfigModel;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.StringItem;
import com.mcreater.amcl.controls.skin.SkinView;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.dialogs.LoadingDialog;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.pages.stages.FXBrowserPage;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
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
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;
        user_object = new SimpleObjectProperty<>();
        mainBox = new VBox();
        offL = new VBox();
        nameItem = new StringItem("", width / 4 * 3);
        offlineSkin = new ListItem<>("", width / 4 * 3);
        offlineLogin = new JFXButton();
        offlineLogin.setFont(Fonts.t_f);
        offlineLogin.setOnAction(event -> {
            Launcher.configReader.configModel.last_userType = "OFFLINE";
            Launcher.configReader.configModel.last_name = nameItem.cont.getText();
            Launcher.configReader.configModel.last_uuid = OffLineUser.STEVE;
            Launcher.configReader.configModel.last_refreshToken = "";
            Launcher.configReader.configModel.last_accessToken = "";
            Launcher.configReader.write();
            user_object.set(new OffLineUser(Launcher.configReader.configModel.last_name, Launcher.configReader.configModel.last_uuid));
            FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.content"), "");
            refreshSkin();
            setP1(2);
        });
        switch (ConfigModel.UserType.valueOf(Launcher.configReader.configModel.last_userType)){
            case OFFLINE:
                if (Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null){
                    nameItem.cont.setText(Launcher.configReader.configModel.last_name);
                    user_object.set(new OffLineUser(Launcher.configReader.configModel.last_name, Launcher.configReader.configModel.last_uuid));
                }
                break;
            case MICROSOFT:
                if (Launcher.configReader.configModel.last_name != null || Launcher.configReader.configModel.last_uuid != null || Launcher.configReader.configModel.last_accessToken != null || Launcher.configReader.configModel.last_refreshToken != null){
                    user_object.set(new MicrosoftUser(Launcher.configReader.configModel.last_accessToken, Launcher.configReader.configModel.last_name, Launcher.configReader.configModel.last_uuid, new Vector<>(), Launcher.configReader.configModel.last_refreshToken));
                }
                break;
        }
        offL.getChildren().addAll(nameItem, offlineLogin);
        msLogin = new JFXButton();
        msLogin.setFont(Fonts.s_f);
        msLogin.setOnAction(event -> {
            FXBrowserPage p = new FXBrowserPage(MSAuth.loginUrl);
            msLogin.setDisable(true);
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
                    user_object.set(p.user);
                    Launcher.configReader.write();
                    Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.userselectpage.login.success.title"), Launcher.languageManager.get("ui.userselectpage.login.success.ms.content"), ""));
                }
                Platform.runLater(() -> msLogin.setDisable(false));
                Platform.runLater(() -> {refreshSkin();setP1(2);});
            }).start();
        });

        view = new ImageView();
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
            Launcher.configReader.write();
        });

        HBox g = new HBox(view, name, refresh, logout);
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
    public void refreshSkin(){
        CountDownLatch latch = new CountDownLatch(1);
        if (user_object.get() instanceof OffLineUser){
            switch (user_object.get().uuid){
                case OffLineUser.STEVE:
                    setImage(SkinView.STEVE);
                    skin3d.updateSkin(SkinView.STEVE, false, new WritableImage(1, 1));
                    latch.countDown();
                    break;
                case OffLineUser.ALEX:
                    setImage(SkinView.ALEX);
                    skin3d.updateSkin(SkinView.ALEX, true, new WritableImage(1, 1));
                    latch.countDown();
                    break;
                default:
                    new Thread(() -> {
                        try {
                            for (MSAuth.McProfileModel.McSkinModel model : MSAuth.getUserSkin(user_object.get().uuid).skins) {
                                Image skin;
                                Image cape;
                                if (model.url.equals("https://")){
                                    if (model.isSlim){
                                        skin = SkinView.ALEX;
                                    }
                                    else {
                                        skin = SkinView.STEVE;
                                    }
                                }
                                else {
                                    skin = new Image(model.url);
                                }

                                if (!model.cape.equals("https://")){
                                    cape = new Image(model.cape);
                                }
                                else {
                                    cape = new WritableImage(1, 1);
                                }
                                Platform.runLater(() -> {
                                    skin3d.updateSkin(skin, model.isSlim, cape);
                                    setImage(skin);
                                });
                                break;
                            }
                        } catch (IOException e) {
                            Platform.runLater(() -> setImage(SkinView.STEVE));
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
                        Image skin;
                        Image cape;
                        if (model.url.equals("https://")){
                            if (model.isSlim){
                                skin = SkinView.ALEX;
                            }
                            else {
                                skin = SkinView.STEVE;
                            }
                        }
                        else {
                            skin = new Image(model.url);
                        }

                        if (!model.cape.equals("https://")){
                            cape = new Image(model.cape);
                        }
                        else {
                            cape = new WritableImage(1, 1);
                        }
                        Platform.runLater(() -> {
                            skin3d.updateSkin(skin, model.isSlim, cape);
                            setImage(skin);
                        });
                        break;
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> setImage(SkinView.STEVE));
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
    public void setImage(Image i){
        WritableImage image = new WritableImage(8, 8);
        try {
            for (int x = 8; x < 16; x++) {
                for (int y = 8; y < 16; y++) {
                    image.getPixelWriter().setArgb(x - 8, y - 8, i.getPixelReader().getArgb(x, y));
                }
            }
        }
        catch (IndexOutOfBoundsException ignored){}

        view.setImage(scrollImage(image, 6, 6));
        view.setFitHeight(view.getImage().getHeight());
        view.setFitWidth(view.getImage().getWidth());
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
    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
