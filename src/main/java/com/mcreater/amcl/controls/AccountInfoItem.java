package com.mcreater.amcl.controls;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.controls.skin.SkinView;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.Callable;

public class AccountInfoItem extends VBox {
    Label userName;
    ImageView headImage;
    ImageView headCoverImage;
    JFXButton modify;
    JFXButton delete;
    JFXButton refresh;
    public JFXRadioButton selector;
    public final AbstractUser user;
    public void setModify(EventHandler<ActionEvent> handler) {
        modify.setOnAction(handler);
    }
    public void setDelete(EventHandler<ActionEvent> handler) {
        delete.setOnAction(handler);
    }
    public void setRefresh(EventHandler<ActionEvent> handler) {
        refresh.setOnAction(handler);
    }
    private static InputStream createInputstreamFromFile(File f) {
        try {
            return Files.newInputStream(f.toPath());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[]{});
        }
    }
    private static ImmutablePair<Image, Image> getHeadImage(AbstractUser user) {
        if (user instanceof OffLineUser) {
            OffLineUser user1 = (OffLineUser) user;
            if (user1.skinUseable()) {
                return new ImmutablePair<>(cutHeadImage(new Image(createInputstreamFromFile(new File(user1.skin))))
                        , cutHeadCoverImage(new Image(createInputstreamFromFile(new File(user1.skin)))));
            }
        }
        switch (user.uuid) {
            case "000000000000300a9d83f9ec9e7fae8e":
                return new ImmutablePair<>(cutHeadImage(SkinView.STEVE), cutHeadCoverImage(SkinView.STEVE));
            case "000000000000300a9d83f9ec9e7fae8d":
                return new ImmutablePair<>(cutHeadImage(SkinView.ALEX), cutHeadCoverImage(SkinView.ALEX));
            default:
                try {
                    Image image = new Image(MSAuth.getUserSkin(user.uuid).skin.url);
                    return new ImmutablePair<>(cutHeadImage(image), cutHeadCoverImage(image));
                }
                catch (Exception e){
                    return new ImmutablePair<>(cutHeadImage(SkinView.STEVE), cutHeadCoverImage(SkinView.STEVE));
                }
        }
    }
    private static int getOff(Image src) {
        return (int) (src.getWidth() / 8);
    }
    private static Image cutHeadImage(Image src) {
        int offset = getOff(src);

        WritableImage result = new WritableImage(offset, offset);
        for (int x = offset; x < offset * 2; x++) {
            for (int y = offset; y < offset * 2; y++) {
                result.getPixelWriter().setColor(
                        x - offset,
                        y - offset,
                        src.getPixelReader().getColor(
                                x, y
                        )
                );
            }
        }
        return result;
    }
    private static Image cutHeadCoverImage(Image src) {
        int offset = getOff(src);
        if (src.getWidth() / src.getHeight() >= 1) {
            WritableImage result = new WritableImage(offset, offset);
            for (int x = offset * 5; x < offset * 6; x++) {
                for (int y = offset; y < offset * 2; y++) {
                    result.getPixelWriter().setColor(
                            x - offset * 5,
                            y - offset,
                            src.getPixelReader().getColor(
                                    x, y
                            )
                    );
                }
            }
            return result;
        }
        else {
            return new WritableImage(offset, offset);
        }
    }
    private static Image smallImage(Image image, double xadd, double yadd) {
        WritableImage image1 = new WritableImage((int) (image.getWidth() * xadd), (int) (image.getHeight() * yadd));
        for (int x = 0;x < image.getWidth() * xadd;x++){
            for (int y = 0;y < image.getHeight() * yadd;y++){
                image1.getPixelWriter().setColor(
                        x,
                        y,
                        image.getPixelReader().getColor(
                                (int) (x / xadd),
                                (int) (y / yadd)
                        )
                );
            }
        }
        return image;
    }
    private static Image scrollImage(Image image, double xadd, double yadd) {
        if (xadd < 1 || yadd < 1) return smallImage(image, xadd, yadd);
        WritableImage image1 = new WritableImage((int) (image.getWidth() * xadd), (int) (image.getHeight() * yadd));
        for (int x = 0;x < image.getWidth();x++){
            for (int y = 0;y < image.getHeight();y++){
                for (int ax = 0;ax < xadd;ax++){
                    for (int ay = 0;ay < yadd;ay++){
                        image1.getPixelWriter().setArgb((int) (x * xadd + ax), (int) (y * yadd + ay), image.getPixelReader().getArgb(x, y));
                    }
                }
            }
        }
        return image1;
    }
    public AccountInfoItem(AbstractUser user, double width) {
        this.user = user;
        userName = new Label(user.username);
        userName.setFont(Fonts.s_f);
        headImage = new ImageView();
        headImage.setFitWidth(32);
        headImage.setFitHeight(32);
        headCoverImage = new ImageView();
        headCoverImage.setFitWidth(32);
        headCoverImage.setFitHeight(32);

        Pane con = new Pane(headImage, headCoverImage);

        new Thread(() -> {
            ImmutablePair<Image, Image> head = getHeadImage(user);
            headImage.setImage(scrollImage(head.getKey(), (int) (32 / head.getKey().getWidth()), (int) (32 / head.getKey().getWidth())));
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(scrollImage(head.getValue(), (int) (32 / head.getValue().getWidth()), (int) (32 / head.getValue().getWidth())), null), "png", new File(user.uuid + ".png"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            headCoverImage.setImage(scrollImage(head.getValue(), (int) (32 / head.getValue().getWidth()), (int) (32 / head.getValue().getWidth())));
        }).start();

        modify = new JFXButton();
        delete = new JFXButton();
        refresh = new JFXButton();

        FXUtils.ControlSize.setAll(30, 30, modify, delete, refresh);

        modify.setGraphic(Launcher.getSVGManager().accountEdit(Bindings.createObjectBinding((Callable<Paint>) () -> Color.BLACK), 30, 30));
        delete.setGraphic(Launcher.getSVGManager().delete(Bindings.createObjectBinding((Callable<Paint>) () -> Color.BLACK), 30, 30));
        refresh.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding(() -> Color.BLACK), 30, 30));

        selector = new JFXRadioButton();
        selector.selectedProperty().addListener((observable, oldValue, newValue) -> user.active = newValue);

        HBox box = new HBox(refresh, modify, delete);
        box.setSpacing(0);

        HBox left = new HBox(new Pane(), selector, con, userName);
        HBox right = new HBox(box);

        left.setAlignment(Pos.CENTER_LEFT);
        left.setSpacing(20);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox topG = new HBox(left, right);
        FXUtils.ControlSize.setWidth(topG, width);

        FXUtils.ControlSize.setWidth(left, width - 95);
        FXUtils.ControlSize.setWidth(right, 95);

        getChildren().add(topG);
    }
}
