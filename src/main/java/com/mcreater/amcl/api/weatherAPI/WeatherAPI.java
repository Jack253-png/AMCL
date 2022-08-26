package com.mcreater.amcl.api.weatherAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcreater.amcl.api.weatherAPI.models.WeatherAPIModel;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;

import java.io.IOException;
import java.util.Map;

public class WeatherAPI {
    public static final String WEATHER_API_URL = "http://autodev.openspeech.cn/csp/api/v2.1/weather";
    public static WeatherAPIModel getWeather(String city) throws IOException {
        Map<Object, Object> data = J8Utils.createMap(
                "openId", "aiuicus",
                "clientType", "android",
                "sign", "android",
                "city", city,
                "needMoreData", true,
                "pageNo", 1,
                "pageSize", 7
        );
        HttpClient client = HttpClient.getInstance(WEATHER_API_URL, data);
        client.openConnection();
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        String content = client.read();
        WeatherAPIModel model = g.fromJson(content, WeatherAPIModel.class);
        return model;
    }
}
