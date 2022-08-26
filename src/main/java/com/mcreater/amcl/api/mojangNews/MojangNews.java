package com.mcreater.amcl.api.mojangNews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.mojangNews.models.NewsModel;
import com.mcreater.amcl.util.net.HttpClient;

import java.io.IOException;

public class MojangNews {
    public static final String url = "https://launchercontent.mojang.com/news.json";
    public static NewsModel getNews() throws IOException {
        HttpClient client = HttpClient.getInstance(url);
        client.openConnection();

        Gson g = new GsonBuilder().setPrettyPrinting().create();

        NewsModel model = g.fromJson(client.read(), NewsModel.class);
        return model;
    }
}
