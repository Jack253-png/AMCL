package com.mcreater.amcl.controls;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class RemoteMod extends HBox {
    public Label name;
    public Label version;
    public Label desc;
    public Label authors;
    public String path;
    public Label filePath;
    public ImageView icon;
    public RemoteMod(CommonModInfoModel model){
        this.path = model.path;
        name = new Label(Objects.equals(model.name, "") ? "null" : model.name);
        name.setFont(Fonts.s_f);
        name.setStyle("word-break:break-all;word-wrap:break-word;");
        version = new Label(Objects.equals(model.version, "") ? "null" : model.version);
        version.setFont(Fonts.t_f);
        version.setStyle("word-break:break-all;word-wrap:break-word;");
        desc = new Label(Objects.equals(model.description, "") ? "null" : model.description);
        desc.setFont(Fonts.t_f);
        desc.setStyle("word-break:break-all;word-wrap:break-word;");
        authors = new Label();
        authors.setText(model.authorList == null ? "null" : String.join(", ", model.authorList));
        authors.setFont(Fonts.t_f);
        authors.setStyle("word-break:break-all;word-wrap:break-word;");
        filePath = new Label(path);
        filePath.setFont(Fonts.t_f);
        filePath.setStyle("word-break:break-all;word-wrap:break-word;");
        icon = new ImageView(model.icon);
        icon.setFitWidth(50);
        icon.setFitHeight(50);
        setAlignment(Pos.TOP_LEFT);
        setSpacing(15);
        this.getChildren().addAll(icon, new VBox(name, filePath, version, desc, authors));
    }
}
