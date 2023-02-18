package com.mcreater.amcl.controls;

import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.Nullable;

import static com.mcreater.amcl.game.VersionTypeGetter.VersionType.*;
import static com.mcreater.amcl.util.svg.Images.*;

public class VersionItem extends HBox {
    ImageView versionType;
    HBox butt;
    Label v;
    String s;
    public VersionItem(String s, @Nullable VersionTypeGetter.VersionType f){
        this.s = s;
        versionType = new ImageView();
        versionType.setFitWidth(40);
        versionType.setFitHeight(40);
        if (f == null) versionType.setImage(original);
        else versionType.setImage(VersionTypeGetter.VersionType.getImage(f));
        butt = new HBox();
        butt.setAlignment(Pos.CENTER_LEFT);
        v = new Label(s);
        v.setFont(Fonts.t_f);
        v.setId("noW");
        butt.getChildren().add(v);
        setSpacing(15);
        getChildren().addAll(versionType, butt);
    }
    public String getVersion(){
        return s;
    }
}
