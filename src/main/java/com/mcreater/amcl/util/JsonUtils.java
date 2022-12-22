package com.mcreater.amcl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public final class JsonUtils {
    public static final Gson GSON_PARSER = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(AbstractUser.class, new TypeAdapter<AbstractUser>() {
                public void write(JsonWriter out, AbstractUser value) throws IOException {
                    if (value instanceof MicrosoftUser) {
                        MicrosoftUser userM = (MicrosoftUser) value;
                        out.beginObject()
                                .name("active").value(userM.active)
                                .name("user_type").value(AbstractUser.MICROSOFT)
                                .name("access_token").value(userM.accessToken)
                                .name("refresh_token").value(userM.refreshToken)
                                .name("uuid").value(userM.uuid)
                                .name("user_name").value(userM.username)
                                .name("skin")
                                .beginObject()
                                    .name("id").value(userM.skin.id)
                                    .name("state").value(userM.skin.state)
                                    .name("url").value(userM.skin.url)
                                    .name("variant").value(userM.skin.variant)
                                    .name("cape").value(userM.skin.cape)
                                    .name("is_slim").value(userM.skin.isSlim)
                                .endObject()
                           .endObject();
                    }
                    else if (value instanceof OffLineUser) {
                        OffLineUser userO = (OffLineUser) value;
                        out.beginObject()
                                .name("active").value(userO.active)
                                .name("user_type").value(AbstractUser.OFFLINE)
                                .name("access_token").value(userO.accessToken)
                                .name("refresh_token").value(userO.refreshToken)
                                .name("uuid").value(userO.uuid)
                                .name("user_name").value(userO.username)
                                .name("custom_skin")
                                .beginObject()
                                    .name("skin").value(userO.skin)
                                    .name("cape").value(userO.cape)
                                    .name("is_slim").value(userO.is_slim)
                                .endObject()
                           .endObject();
                    }
                }

                public AbstractUser read(JsonReader in) throws IOException {
                    JsonUtils.JsonProcessors.JsonToMapProcessor processor = new JsonUtils.JsonProcessors.JsonToMapProcessor(in);
                    while (processor.processable()) {
                        processor.process();
                    }
                    Map<String, Object> content = processor.getProcessedContent();

                    boolean active = JsonUtils.JsonProcessors.parseBoolean(JsonUtils.JsonProcessors.getValue(content, "active"));
                    int user_type = (int) JsonUtils.JsonProcessors.parseLong(JsonUtils.JsonProcessors.getValue(content, "user_type"));
                    String access_token = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "access_token"));
                    String refresh_token = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "refresh_token"));
                    String uuid = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "uuid"));
                    String user_name = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "user_name"));

                    String custom_skin_skin = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "custom_skin", "skin"));
                    String custom_skin_cape = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "custom_skin", "cape"));
                    boolean custom_skin_is_slim = JsonUtils.JsonProcessors.parseBoolean(JsonUtils.JsonProcessors.getValue(content, "custom_skin", "is_slim"));

                    String skin_id = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "skin", "id"));
                    String skin_state = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "skin", "state"));
                    String skin_url = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "skin", "url"));
                    String skin_variant = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "skin", "variant"));
                    String skin_cape = JsonUtils.JsonProcessors.parseString(JsonUtils.JsonProcessors.getValue(content, "skin", "cape"));
                    boolean skin_is_slim = JsonUtils.JsonProcessors.parseBoolean(JsonUtils.JsonProcessors.getValue(content, "skin", "is_slim"));

                    if (user_type == 1) {
                        MSAuth.McProfileModel.McSkinModel model = new MSAuth.McProfileModel.McSkinModel();
                        model.isSlim = skin_is_slim;
                        model.id = skin_id;
                        model.cape = skin_cape;
                        model.url = skin_url;
                        model.state = skin_state;
                        model.variant = skin_variant;
                        return new MicrosoftUser(access_token, user_name, uuid, model, refresh_token);
                    }
                    else {
                        return new OffLineUser(user_name, uuid, custom_skin_is_slim, custom_skin_skin, custom_skin_cape);
                    }
                }
            })
            .registerTypeAdapter(LanguageManager.LanguageType.class, new TypeAdapter<LanguageManager.LanguageType>() {
                public void write(JsonWriter out, LanguageManager.LanguageType value) throws IOException {
                    out.value(value.toString());
                }

                public LanguageManager.LanguageType read(JsonReader in) {
                    try {
                        return LanguageManager.LanguageType.valueOf(in.nextString());
                    }
                    catch (Exception e) {
                        return LanguageManager.LanguageType.valueOf(LocateHelper.get());
                    }
                }
            })
            .registerTypeAdapter(FasterUrls.Servers.class, new TypeAdapter<FasterUrls.Servers>() {
                public void write(JsonWriter out, FasterUrls.Servers value) throws IOException {
                    out.value(value.toString());
                }

                public FasterUrls.Servers read(JsonReader in) {
                    try {
                        return FasterUrls.Servers.valueOf(in.nextString());
                    }
                    catch (Exception e) {
                        return FasterUrls.Servers.MCBBS;
                    }
                }
            })
            .create();
    public static class JsonProcessors {
        public static Object getValue(Map<String, ?> map, String... index) {
            Object tempObject = map;
            for (String key : index) {
                boolean isArrayIndex = key.startsWith("[") && key.endsWith("]");
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
        public static String parseString(Object o) {
            return o == null ? "" : (o instanceof String ? (String) o : "");
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
