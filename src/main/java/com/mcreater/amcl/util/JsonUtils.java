package com.mcreater.amcl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

public final class JsonUtils {

    public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    private JsonUtils() {
    }

    public static <T> T fromNonNullJson(String json, Class<T> classOfT) throws JsonParseException {
        T parsed = GSON.fromJson(json, classOfT);
        if (parsed == null)
            throw new JsonParseException("Json object cannot be null.");
        return parsed;
    }

    public static <T> T fromNonNullJson(String json, Type type) throws JsonParseException {
        T parsed = GSON.fromJson(json, type);
        if (parsed == null)
            throw new JsonParseException("Json object cannot be null.");
        return parsed;
    }

    public static <T> T fromMaybeMalformedJson(String json, Class<T> classOfT) throws JsonParseException {
        try {
            return GSON.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static <T> T fromMaybeMalformedJson(String json, Type type) throws JsonParseException {
        try {
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
    public static boolean isVaildJson(File path){
        try {
            GSON.fromJson(FileUtils.FileStringReader.read(path.getAbsolutePath()), Map.class);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
