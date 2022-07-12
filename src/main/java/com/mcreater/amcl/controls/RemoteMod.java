package com.mcreater.amcl.controls;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RemoteMod extends VBox {
    public Label name;
    public Label version;
    public Label desc;
    public Label authors;
    public RemoteMod(CommonModInfoModel model){
        name = new Label(model.name);
        name.setFont(Fonts.s_f);
        name.setStyle("word-break:break-all;word-wrap:break-word;");
        version = new Label(model.version);
        version.setFont(Fonts.t_f);
        version.setStyle("word-break:break-all;word-wrap:break-word;");
        desc = new Label(model.description);
        desc.setFont(Fonts.t_f);
        desc.setStyle("word-break:break-all;word-wrap:break-word;");
        authors = new Label();
        if (model.authorList != null) authors.setText(model.authorList.toString());
        authors.setFont(Fonts.t_f);
        authors.setStyle("word-break:break-all;word-wrap:break-word;");
        this.getChildren().addAll(name, version, desc, authors);
    }
}
