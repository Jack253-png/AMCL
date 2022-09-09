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
    public String path;
    public Label filePath;
    public RemoteMod(CommonModInfoModel model){
        this.path = model.path;
        name = new Label(model.name.equals("") ? "null" : model.name);
        name.setFont(Fonts.s_f);
        name.setStyle("word-break:break-all;word-wrap:break-word;");
        version = new Label(model.version.equals("") ? "null" : model.version);
        version.setFont(Fonts.t_f);
        version.setStyle("word-break:break-all;word-wrap:break-word;");
        desc = new Label(model.description.equals("") ? "null" : model.description);
        desc.setFont(Fonts.t_f);
        desc.setStyle("word-break:break-all;word-wrap:break-word;");
        authors = new Label();
        authors.setText(model.authorList == null ? "null" : String.join(", ", model.authorList));
        authors.setFont(Fonts.t_f);
        authors.setStyle("word-break:break-all;word-wrap:break-word;");
        filePath = new Label(path);
        filePath.setFont(Fonts.t_f);
        filePath.setStyle("word-break:break-all;word-wrap:break-word;");
        this.getChildren().addAll(name, filePath, version, desc, authors);
    }
}
