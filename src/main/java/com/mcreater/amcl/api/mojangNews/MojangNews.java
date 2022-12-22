package com.mcreater.amcl.api.mojangNews;

import com.mcreater.amcl.api.mojangNews.models.NewsModel;
import com.mcreater.amcl.util.net.HttpClient;

import java.io.IOException;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class MojangNews {
    public static final String url = "https://launchercontent.mojang.com/news.json";
    public static NewsModel getNews() throws IOException {
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();

        return GSON_PARSER.fromJson(client.read(), NewsModel.class);
    }
}
