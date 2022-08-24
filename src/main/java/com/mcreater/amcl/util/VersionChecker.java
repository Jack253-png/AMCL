package com.mcreater.amcl.util;

import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.pages.dialogs.PopupMessage;
import com.mcreater.amcl.pages.stages.UpgradePage;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;

import static com.mcreater.amcl.Launcher.languageManager;

public class VersionChecker {
    public static void check(){
        try {
            if (GithubReleases.isDevelop()) {
                PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.inDevelope"), PopupMessage.MessageTypes.LABEL, null);
            } else if (GithubReleases.outDated()) {
                Runnable show = () -> new UpgradePage().open();
                Platform.runLater(() -> {
                    Hyperlink link = (Hyperlink) PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.outDated"), PopupMessage.MessageTypes.HYPERLINK, null);
                    link.setOnAction(event -> show.run());
                });
            }
            else {
                PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.latest"), PopupMessage.MessageTypes.LABEL, null);
            }
        }
        catch (IllegalStateException e){
            Platform.runLater(() -> PopupMessage.createMessage(languageManager.get("ui.mainpage.versionChecker.checkFailed.name"), PopupMessage.MessageTypes.LABEL, null));
        }
    }
}
