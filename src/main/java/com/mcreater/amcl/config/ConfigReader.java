package com.mcreater.amcl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ConfigReader {
    boolean first_config;
    File file;
    public static final Gson GSON_PRASER = new GsonBuilder()
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
                                    .name("cape_url").value(userM.skin.cape)
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
                                .name("custom_skin").beginObject()
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


                    return new OffLineUser("000", "0000", false, null, null);
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


    public ConfigReader(File f) throws IOException {
        first_config = false;
        if (!f.getPath().endsWith(".json")){
            throw new IllegalStateException("Unsupported file endswith");
        }
        if (!f.exists()){
            boolean e = f.createNewFile();
            if (!e){
                throw new IllegalStateException("Null to create config file");
            }
            first_config = true;
        }
        file = f;
        if (first_config){
            writeDefault();
        }
    }
    public void writeDefault(){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(ConfigModel.getDefault(), ConfigModel.class));
            fileWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public ConfigModel read() throws IOException {
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                result.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Read Config File Failed");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        if (!result.toString().equals("")) {
            try {
                return GSON_PRASER.fromJson(result.toString(), ConfigModel.class);
            }
            catch (Exception e){
                e.printStackTrace();
                writeDefault();
                return new ConfigReader(file).read();
            }
        }
        else{
            writeDefault();
            return new ConfigReader(file).read();
        }
    }
}
