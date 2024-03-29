package com.mcreater.amcl.api.IPApi;

import com.mcreater.amcl.util.net.HttpClient;

public class IPApi {
    public static final String url = "http://ip-api.com/json";

    public static class LocateJsonModel {
        public String status;
        public String country;
        public String countryCode;
        public String region;
        public String regionName;
        public String city;
        public String zip;
        public double lat;
        public double lon;
        public String timezone;
        public String isp;
        public String org;
        public String as;
        public String query;
    }

    public static LocateJsonModel getLocate() throws Exception {
        return HttpClient.getInstance(url).open().toJson(LocateJsonModel.class);
    }
}
