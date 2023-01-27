package com.mcreater.amcl.api.githubApi;

import com.mcreater.amcl.api.githubApi.models.ReleaseModel;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.net.HttpClient;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class GithubReleases {
    private static final String api_url = "https://api.github.com";

    public static List<ReleaseModel> getReleases() {
        try {
            String url = String.format("%s/repos/Jack253-png/AMCL/releases", api_url);
            List<ReleaseModel> releases = JsonUtils.readArray(
                    HttpClient.getInstance(url, J8Utils.createMap("per_page", Integer.MAX_VALUE))
                            .open()
                            .timeout(15000)
                            .method(HttpClient.Method.GET)
                            .readWithNoLog()
                    , ReleaseModel.class);

            int h = getVersionsBehind(releases, VersionInfo.launcher_version);
            for (ReleaseModel model : releases) {
                int i = -1;
                for (int index = 0; index < releases.size(); index++) {
                    if (Objects.equals(releases.get(index).tag_name, model.tag_name)) {
                        i = index;
                    }
                }
                model.outdated = !(i <= h - 1);
                model.iscurrent = i == h;
            }
            return releases;
        } catch (Exception e) {
            return new Vector<>();
        }
    }

    public static int getVersionsBehind() {
        return getVersionsBehind(GithubReleases.getReleases(), VersionInfo.launcher_version);
    }

    public static int getVersionsBehind(List<ReleaseModel> result, String node_name) {
        int i = -1;
        for (int index = 0; index < result.size(); index++) {
            if (Objects.equals(result.get(index).tag_name, node_name)) {
                i = index;
            }
        }
        return i;
    }

    public static boolean outDated() {
        return (getVersionsBehind() != 0) && !isDevelop();
    }

    public static boolean isDevelop() {
        return getVersionsBehind() < 0;
    }
}
