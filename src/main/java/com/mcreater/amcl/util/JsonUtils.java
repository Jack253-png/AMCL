package com.mcreater.amcl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public final class JsonUtils {
    public static class JsonProcessors {
//        public static Object getValue(Map<String, ?> map, String... key) {
//            Object tempObject = map;
//            for (int index = 0; index < key.length; index++) {
//                try {
//                    tempObject = ((Map<?, ?>) tempObject).get(key[index]);
//                }
//                catch (Exception e) {
//                    return null;
//                }
//            }
//            return tempObject;
//        }
        public static Object getValue(Map<String, ?> map, String index) {
            Object tempObject = map;
            for (String key : index.split("\\.")) {
                boolean isArrayIndex = index.startsWith("[") && index.endsWith("]");
                boolean isArray = tempObject instanceof Collection<?>;
                boolean isMap = tempObject instanceof Map<?, ?>;
                if (isArray != isArrayIndex) {
                    return null;
                }

                if (isArray) {
                    try {
                        int Arrindex = Integer.parseInt(key.replace("[", "").replace("]", ""));
                        tempObject = ((Vector<?>) tempObject).get(Arrindex);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                else if (isMap) {
                    tempObject = ((Map<?, ?>) tempObject).get(key);
                }
                else {
                    return null;
                }

                if (tempObject == null) return null;
            }
            return tempObject;
        }
        public static boolean parseBoolean(Object o) {
            return o != null && (Boolean) o;
        }
        public static long parseLong(Object o) {
            return o == null ? 0 : (Long) o;
        }
        public static abstract class JsonProcessor {
            JsonReader reader;
            Stack<Object> objectStack = new Stack<>();
            String name;
            public JsonProcessor(JsonReader reader) {
                this.reader = reader;
            }
            void putValue(Object o) {
                Object cont = objectStack.pop();
                objectStack.push(cont);
                if (cont instanceof Collection<?>) {
                    ((Collection<Object>) cont).add(o);
                }
                else if (cont instanceof Map<?, ?>) {
                    ((Map<String, Object>) cont).put(name, o);
                }
                name = "null";
            }
            public void process() throws IOException {
                JsonToken token = reader.peek();
                switch (token) {
                    case NULL:
                        reader.nextNull();
                        putValue(null);
                        break;
                    case NAME:
                        name = reader.nextName();
                        break;
                    case STRING:
                        putValue(reader.nextString());
                        break;
                    case NUMBER:
                        putValue(reader.nextLong());
                        break;
                    case BOOLEAN:
                        putValue(reader.nextBoolean());
                        break;
                    case BEGIN_OBJECT:
                        reader.beginObject();
                        Map<String, Object> content2 = new HashMap<>();
                        putValue(content2);
                        objectStack.push(content2);
                        break;
                    case END_OBJECT:
                        reader.endObject();
                        objectStack.pop();
                        break;
                    case BEGIN_ARRAY:
                        reader.beginArray();
                        Collection<Object> content3 = new Vector<>();
                        putValue(content3);
                        objectStack.push(content3);
                        break;
                    case END_ARRAY:
                        reader.endArray();
                        objectStack.pop();
                        break;
                    default:
                    case END_DOCUMENT:
                        break;
                }
            }
            public boolean processable() {
                return objectStack.size() > 0;
            }
            public abstract Object getProcessedContent();
        }
        public static class JsonToCollectionProcessor extends JsonProcessor {
            private final Collection<Object> content = new Vector<>();
            public JsonToCollectionProcessor(JsonReader reader) throws IOException {
                super(reader);
                if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                    objectStack.push(content);
                    reader.beginArray();
                }
                else {
                    throw new IOException("Not a array");
                }
            }
            public Collection<Object> getProcessedContent() {
                return content;
            }
        }
        public static class JsonToMapProcessor extends JsonProcessor {
            private final Map<String, Object> content = new HashMap<>();
            public JsonToMapProcessor(JsonReader reader) throws IOException {
                super(reader);
                this.reader = reader;
                if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                    objectStack.push(content);
                    reader.beginObject();
                }
                else {
                    throw new IOException("Not a map");
                }
            }
            public Map<String, Object> getProcessedContent() {
                return content;
            }
        }
    }
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
