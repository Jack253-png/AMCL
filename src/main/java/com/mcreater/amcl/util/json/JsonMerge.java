package com.mcreater.amcl.util.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonMerge {
    public static JSONObject jsonMerge(JSONObject source, JSONObject target) {
        if (target == null) {
            return source;
        }

        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject valueJson) {
                    JSONObject targetValue = jsonMerge(valueJson, target.getJSONObject(key));
                    target.put(key, targetValue);
                } else if (value instanceof JSONArray valueArray) {
                    for (int i = 0; i < valueArray.size(); i++) {
                        JSONObject obj = (JSONObject) valueArray.get(i);
                        JSONObject targetValue = jsonMerge(obj, (JSONObject) target.getJSONArray(key).get(i));
                        target.getJSONArray(key).set(i, targetValue);
                    }
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }
}
