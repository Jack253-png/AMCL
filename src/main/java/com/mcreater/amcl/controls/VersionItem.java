package com.mcreater.amcl.controls;

import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.mcreater.amcl.util.svg.Images.*;

public class VersionItem extends HBox {
    ImageView versionType;
    HBox butt;
    Label v;
    String s;
    public VersionItem(String s, String f){
        this.s = s;
        versionType = new ImageView();
        versionType.setFitWidth(40);
        versionType.setFitHeight(40);
        switch (f) {
            case "original":
                versionType.setImage(original);
                break;
            case "forge":
            case "forge-optifine":
                versionType.setImage(forge);
                break;
            case "fabric":
                versionType.setImage(fabric);
                break;
            case "liteloader":
                versionType.setImage(liteloader);
                break;
            case "optifine":
                versionType.setImage(optifine);
                break;
        }
        butt = new HBox();
        butt.setAlignment(Pos.CENTER_LEFT);
         v = new Label(s);
        v.setFont(Fonts.t_f);
        butt.getChildren().add(v);
        setSpacing(15);
        getChildren().addAll(versionType, butt);
    }
    public String getVersion(){
        return s;
    }
}
