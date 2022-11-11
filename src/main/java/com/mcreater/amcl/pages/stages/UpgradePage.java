package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.api.githubApi.models.AssetsModel;
import com.mcreater.amcl.api.githubApi.models.ReleaseModel;
import com.mcreater.amcl.controls.UpdateItem;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.svg.Icons;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UpgradePage extends AbstractStage {
    public AdvancedScrollPane content;
    public VBox c;
    public UpgradePage(){
        this.setTitle(Launcher.languageManager.get("ui.upgradepage.title"));
        this.getIcons().add(Icons.fxIcon.get());
        c = new VBox();
        for (ReleaseModel model : GithubReleases.getReleases()) {
            VBox b = new VBox();
            Label desc = new Label(model.body);
            desc.setFont(Fonts.t_f);
            desc.setStyle("word-break:break-all;word-wrap:break-word;");
            b.getChildren().add(desc);
            for (AssetsModel m : model.assets){
                List<String> l = J8Utils.createList(m.name.split("\\."));
                b.getChildren().add(new UpdateItem(l.get(l.size() - 1), m.browser_download_url));
            }
            TitledPane pane = new TitledPane(String.format("%s %s", model.tag_name, Launcher.languageManager.get(String.format("ui.mainpage.versionChecker.isCurrent.%s", model.iscurrent))), b);
            pane.setAnimated(true);
            pane.setId(model.prerelease ? "update-is-prerelease" : "update-is-not-prerelease");
            ThemeManager.applyNode(pane);

            FXUtils.ControlSize.setWidth(pane, 800);
            pane.setExpanded(false);
            pane.setDisable(model.outdated);
            c.getChildren().add(pane);
        }
        content = new AdvancedScrollPane(800, 500, c, false);

        ThemeManager.applyNode(content);
        this.setWidth(800);
        this.setHeight(600);
        this.setResizable(false);
    }
    public void open() {
        Scene s = new Scene(content);
        this.setScene(s);
        this.show();
    }
}
