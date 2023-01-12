package com.mcreater.amcl.controls;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.modApi.common.AbstractModModel;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModAuthorModel;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModCategorieModel;
import com.mcreater.amcl.api.modApi.curseforge.mod.CurseModModel;
import com.mcreater.amcl.api.modApi.modrinth.mod.ModrinthModModel;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.os.SystemActions;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class RemoteMod extends GridPane {
    public AbstractModModel model;
    Label base;
    public Label name;
    public Label desc;
    public HBox b;
    public HBox authors;
    public ImageView image;
    public RemoteMod(AbstractModModel model){
        this.model = model;
        if (model.isCurseMod()) {
            CurseModModel raw = model.toCurseMod();
            String logo_url = raw.logo.thumbnailUrl;
            image = new ImageView();
            image.setFitWidth(40);
            image.setFitHeight(40);
            new Thread(() -> image.setImage(new Image(logo_url))).start();
            name = new Label(raw.name);
            name.setFont(Fonts.s_f);
            desc = new Label(raw.summary);
            desc.setFont(Fonts.t_f);
            desc.setMaxWidth(Launcher.width - 20 * 4 - 35);
            b = new HBox();
            for (int i = 0; i < raw.categories.size(); i++) {
                ImageView view = new ImageView();
                view.setFitWidth(20);
                view.setFitHeight(20);
                b.getChildren().add(view);
            }
            for (int i = 0; i < b.getChildren().size(); i++) {
                ImageView v = (ImageView) b.getChildren().get(i);
                CurseModCategorieModel m = raw.categories.get(i);
                new Thread(() -> v.setImage(new Image(m.iconUrl))).start();
            }
            authors = new HBox();
            base = new Label(Launcher.languageManager.get("ui.addmodspage.mod.authors.name"));
            base.setFont(Fonts.t_f);
            authors.getChildren().add(base);
            for (CurseModAuthorModel m : raw.authors) {
                Hyperlink link = new Hyperlink(m.name);
                link.setOnAction(event -> SystemActions.openBrowser(m.url));
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
        else {
            ModrinthModModel raw = model.toModrinthMod();
            String logo_url = raw.icon_url;
            image = new ImageView();
            image.setFitWidth(40);
            image.setFitHeight(40);
            new Thread(() -> image.setImage(new Image(logo_url))).start();
            name = new Label(raw.title);
            name.setFont(Fonts.s_f);
            desc = new Label(raw.description);
            desc.setFont(Fonts.t_f);
            desc.setMaxWidth(Launcher.width - 20 * 4 - 35);
            authors = new HBox();
            base = new Label(Launcher.languageManager.get("ui.addmodspage.mod.authors.name"));
            base.setFont(Fonts.t_f);
            authors.getChildren().add(base);
            Label aut = new Label(raw.author);
            aut.setFont(Fonts.t_f);
            authors.getChildren().addAll(aut);
            authors.setAlignment(Pos.CENTER_LEFT);
            this.setHgap(20);
            this.setVgap(8);
            this.add(image, 0, 0, 1, 1);
            this.add(name, 1, 0, 1, 1);
            this.add(desc, 1, 1, 1, 1);
            if (raw.author != null) this.add(authors, 1, 2, 1, 1);
        }
    }
    public void refreshLang(){
        base.setText(Launcher.languageManager.get("ui.addmodspage.mod.authors.name"));
    }
}
