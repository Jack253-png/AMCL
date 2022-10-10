package com.mcreater.amcl.controls.items;

import com.jfoenix.controls.JFXToggleButton;
import com.mcreater.amcl.controls.ListItem;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static com.mcreater.amcl.util.svg.Images.fabric;
import static com.mcreater.amcl.util.svg.Images.forge;
import static com.mcreater.amcl.util.svg.Images.liteloader;
import static com.mcreater.amcl.util.svg.Images.optifine;
import static com.mcreater.amcl.util.svg.Images.original;

public class BooleanListItem<T extends Region> extends VBox {
    public Label name;
    public ListItem<T> cont;
    public JFXToggleButton button;
    public ImageView versionType;
    public BooleanListItem (String name, double width, VersionTypeGetter.VersionType type){
        versionType = new ImageView();
        this.name = new Label(name);
        this.name.setFont(Fonts.t_f);
        cont = new ListItem<>(width, 200);
        cont.setStyle("-fx-background-color: transparent");
        cont.page.setStyle("-fx-background-color: transparent");
        button = new JFXToggleButton();
        versionType.setImage(VersionTypeGetter.VersionType.getImage(type));
        versionType.setFitHeight(50);
        versionType.setFitWidth(50);
        HBox box = new HBox(versionType, this.name);
        box.setSpacing(10);
        this.getChildren().addAll(box, button, cont.pane);
    }
}
