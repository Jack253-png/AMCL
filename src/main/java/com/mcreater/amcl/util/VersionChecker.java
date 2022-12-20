package com.mcreater.amcl.util;

import com.mcreater.amcl.api.githubApi.GithubReleases;

import java.util.function.BiConsumer;

import static com.mcreater.amcl.Launcher.languageManager;

public class VersionChecker {
    public static void check(BiConsumer<String, Boolean> updater) {
        try {
            if (GithubReleases.isDevelop()) {
                updater.accept(languageManager.get("ui.mainpage.versionChecker.inDevelope"), false);
            } else if (GithubReleases.outDated()) {
                updater.accept(languageManager.get("ui.mainpage.versionChecker.outDated"), true);
            }
            else {
                updater.accept(languageManager.get("ui.mainpage.versionChecker.latest"), false);
            }
        }
        catch (IllegalStateException e){
            updater.accept(languageManager.get("ui.mainpage.versionChecker.checkFailed.name"), false);
        }
    }
}
