package com.mcreater.amcl.api.weatherAPI;

import com.mcreater.amcl.api.weatherAPI.models.WeatherAPIModel;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.HttpClient;

public class WeatherAPI {
    public static final String WEATHER_API_URL = "http://autodev.openspeech.cn/csp/api/v2.1/weather";

    public static WeatherAPIModel getWeather(String city, int pageNo, int pageSize, boolean moreData) throws Exception {
        return HttpClient.getInstance(WEATHER_API_URL, J8Utils.createMap(
                        "openId", "aiuicus",
                        "clientType", "android",
                        "sign", "android",
                        "city", city,
                        "needMoreData", moreData,
                        "pageNo", pageNo,
                        "pageSize", pageSize
                ))
                .open()
                .toJson(WeatherAPIModel.class);
    }
}
