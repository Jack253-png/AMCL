package com.mcreater.amcl.api.mojangNews;

import com.mcreater.amcl.api.mojangNews.models.NewsModel;
import com.mcreater.amcl.util.net.HttpClient;

public class MojangNews {
    public static final String url = "https://launchercontent.mojang.com/news.json";

    public static NewsModel getNews() throws Exception {
        return HttpClient.getInstance(url).open().toJson(NewsModel.class);
    }
}
