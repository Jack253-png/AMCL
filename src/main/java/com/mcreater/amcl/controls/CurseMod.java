package com.mcreater.amcl.controls;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.curseApi.mod.CurseModAuthorModel;
import com.mcreater.amcl.api.curseApi.mod.CurseModCategorieModel;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.BrowserHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class CurseMod extends GridPane {
    public CurseModModel model;
    Label base;
    public CurseMod(CurseModModel model){
        this.model = model;
        String logo_url = model.logo.thumbnailUrl;
        ImageView image = new ImageView();
        image.setFitWidth(40);
        image.setFitHeight(40);
        new Thread(() -> image.setImage(new Image(logo_url))).start();
        Label name = new Label(model.name);
        name.setFont(Fonts.s_f);
        Label desc = new Label(model.summary);
        desc.setFont(Fonts.t_f);
        desc.setMaxWidth(Launcher.width - 20 * 4 - 35);
        HBox b = new HBox();
        for (int i = 0;i < model.categories.size();i++){
            ImageView view = new ImageView();
            view.setFitWidth(20);
            view.setFitHeight(20);
            b.getChildren().add(view);
        }
        for (int i = 0;i < b.getChildren().size();i++){
            ImageView v = (ImageView) b.getChildren().get(i);
            CurseModCategorieModel m = model.categories.get(i);
            new Thread(() -> v.setImage(new Image(m.iconUrl))).start();
        }
        HBox authors = new HBox();
        base = new Label(Launcher.languageManager.get("ui.addmodspage.mod.authors.name"));
        base.setFont(Fonts.t_f);
        authors.getChildren().add(base);
        for (CurseModAuthorModel m : model.authors){
            Hyperlink link = new Hyperlink(m.name);
            link.setOnAction(event -> BrowserHelper.open(m.url));
            link.setFont(Fonts.t_f);
            Label comma = new Label(",");
            authors.getChildren().addAll(link, comma);
        }
        authors.getChildren().remove(authors.getChildren().size() - 1);
        authors.setAlignment(Pos.CENTER_LEFT);
        this.setHgap(20);
        this.setVgap(8);
        this.add(image, 0, 0, 1, 1);
        this.add(name, 1, 0, 1, 1);
        this.add(desc, 1, 1, 1, 1);
        this.add(authors, 1, 2, 1, 1);
        this.add(b, 1, 3, 1, 1);
    }
    public void refreshLang(){
        base.setText(Launcher.languageManager.get("ui.addmodspage.mod.authors.name"));
    }
}
