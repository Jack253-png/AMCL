package com.mcreater.amcl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;

import java.io.*;

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
                                .name("user_type").value(1)
                                .name("access_token").value(userM.accessToken)
                                .name("refresh_token").value(userM.refreshToken)
                                .name("uuid").value(userM.uuid)
                                .name("user_name").value(userM.username)
                                .name("skin").beginObject()
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
                                .name("user_type").value(0)
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
                    String last_name = "<empty>";
                    int user_type = -1;

                    String access_token = null;
                    String refresh_token = null;
                    String uuid = null;
                    String user_name = null;

                    String custom_skin = null;
                    String custom_cape = null;
                    boolean custom_is_slim = false;

                    String skin_id = null;
                    String skin_state = null;
                    String skin_url = null;
                    String skin_variant = null;
                    String skin_cape = null;
                    boolean skin_is_slim = false;

                    while (in.peek() != JsonToken.END_ARRAY) {
                        try {
                            JsonToken tk = in.peek();
                            switch (tk) {
                                default:
                                case NAME:
                                    last_name = in.nextName();
                                    break;
                                case NUMBER:
                                    if ("user_type".equals(last_name)) {
                                        user_type = in.nextInt();
                                    }
                                    else {
                                        in.nextLong();
                                    }
                                    break;
                                case NULL:
                                    in.nextNull();
                                    break;
                                case STRING:
                                    switch (last_name) {
                                        case "access_token":
                                            access_token = in.nextString();
                                            break;
                                        case "refresh_token":
                                            refresh_token = in.nextString();
                                            break;
                                        case "uuid":
                                            uuid = in.nextString();
                                            break;
                                        case "user_name":
                                            user_name = in.nextString();
                                            break;
                                        case "skin":
                                            custom_skin = in.nextString();
                                            break;
                                        case "cape":
                                            custom_cape = in.nextString();
                                            break;
                                        case "id":
                                            skin_id = in.nextString();
                                            break;
                                        case "state":
                                            skin_state = in.nextString();
                                            break;
                                        case "url":
                                            skin_url = in.nextString();
                                            break;
                                        case "variant":
                                            skin_variant = in.nextString();
                                            break;
                                        case "cape_url":
                                            skin_cape = in.nextString();
                                            break;
                                        default:
                                            in.nextString();
                                            break;
                                    }
                                    break;
                                case BOOLEAN:
                                    switch (last_name) {
                                        case "is_slim":
                                            boolean b = in.nextBoolean();
                                            custom_is_slim = b;
                                            skin_is_slim = b;
                                            break;
                                        default:
                                            in.nextBoolean();
                                            break;
                                    }
                                    break;
                                case BEGIN_ARRAY:
                                    in.beginArray();
                                    break;
                                case BEGIN_OBJECT:
                                    in.beginObject();
                                    break;
                                case END_ARRAY:
                                    in.endArray();
                                    break;
                                case END_OBJECT:
                                    in.endObject();
                                    break;
                                case END_DOCUMENT:
                                    in.close();
                                    break;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            in.skipValue();
                        }
                    }
                    System.out.println(user_type);
                    switch (user_type) {
                        case 0:
                            return new OffLineUser(user_name, uuid, custom_is_slim, custom_skin, custom_cape);
                        case 1:
                            MSAuth.McProfileModel.McSkinModel model = new MSAuth.McProfileModel.McSkinModel();
                            model.isSlim = skin_is_slim;
                            model.id = skin_id;
                            model.url = skin_url;
                            model.cape = skin_cape;
                            model.state = skin_state;
                            model.variant = skin_variant;
                            return new MicrosoftUser(access_token, user_name, uuid, model, refresh_token);
                        default:
                            return null;
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
