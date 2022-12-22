package com.mcreater.amcl.util.xml;

import com.google.gson.annotations.SerializedName;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.patcher.ClassPathInjector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class DepenciesXMLHandler {
    public static Vector<DepencyItem> load() throws Exception {
        return load(ResourceGetter.get("assets/depencies.json"));
    }
    public static Vector<DepencyItem> load(InputStream is) throws Exception {
        DepencyModel model = GSON_PARSER.fromJson(new InputStreamReader(is), DepencyModel.class);
        Vector<DepencyItem> items = new Vector<>();
        for (DepencyModel.ItemModel item : model.depencies){
            if (item.isMultiPlatform) {
                switch (OSInfo.getOSType()) {
                    default:
                    case WINDOWS:
                        items.add(new DepencyItem(item.name, model.maven));
                        break;
                    case WINDOWS_X86:
                        items.add(new DepencyItem(item.winX86, model.maven));
                        break;
                    case MACOS:
                        items.add(new DepencyItem(item.mac, model.maven));
                        break;
                    case MACOS_ARM64:
                        items.add(new DepencyItem(item.macArm64, model.maven));
                        break;
                    case LINUX:
                        items.add(new DepencyItem(item.linux, model.maven));
                        break;
                    case LINUX_ARM64:
                        items.add(new DepencyItem(item.linuxArm64, model.maven));
                        break;
                    case WINDOWS_ARM:
                        items.add(new DepencyItem(item.windowsArm, model.maven));
                        break;
                    case LINUX_ARM32:
                        items.add(new DepencyItem(item.linuxArm32, model.maven));
                        break;
                    case LINUX_LOONGARCH64_OW:
                        items.add(new DepencyItem(item.linuxLoongarch, model.maven));
                        break;
                }
            }
            else {
                if (ClassPathInjector.version < 9 && item.old != null) {
                    items.add(new DepencyItem(item.old, model.maven));
                }
                else {
                    items.add(new DepencyItem(item.name, model.maven));
                }
            }
        }
        return items;
    }
    public static class DepencyModel {
        public String maven;
        public List<ItemModel> depencies;
        public static class ItemModel {
            public boolean isMultiPlatform;
            public String name;
            public String old;
            public String mac;
            public String linux;
            @SerializedName("win-x86")
            public String winX86;
            @SerializedName("mac-arm64")
            public String macArm64;
            @SerializedName("linux-arm32")
            public String linuxArm32;
            @SerializedName("linux-arm64")
            public String linuxArm64;
            @SerializedName("windows-arm")
            public String windowsArm;
            @SerializedName("linux-loongarch")
            public String linuxLoongarch;
        }
    }
}
