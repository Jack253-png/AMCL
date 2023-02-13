package com.mcreater.amcl.util.parsers;

import com.google.gson.annotations.SerializedName;
import com.mcreater.amcl.natives.OSInfo;
import com.mcreater.amcl.natives.ResourceGetter;
import com.mcreater.amcl.patcher.ClassPathInjector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class DepenciesJsonHandler {
    public static Vector<DepencyItem> load() throws Exception {
        return load(ResourceGetter.get("assets/depencies.json"));
    }

    public static Vector<DepencyItem> load(InputStream is) throws Exception {
        DepencyModel model = GSON_PARSER.fromJson(new InputStreamReader(is), DepencyModel.class);
        Vector<DepencyItem> items = new Vector<>();
        for (DepencyModel.ItemModel item : model.depencies) {
            String mav = item.maven != null ? item.mac : model.maven;
            if (item.isMultiPlatform) {
                switch (OSInfo.getOSType()) {
                    default:
                    case WINDOWS:
                        items.add(new DepencyItem(item.name, mav));
                        break;
                    case WINDOWS_X86:
                        items.add(new DepencyItem(item.winX86, mav));
                        break;
                    case MACOS:
                        items.add(new DepencyItem(item.mac, mav));
                        break;
                    case MACOS_ARM64:
                        items.add(new DepencyItem(item.macArm64, mav));
                        break;
                    case LINUX:
                        items.add(new DepencyItem(item.linux, mav));
                        break;
                    case LINUX_ARM64:
                        items.add(new DepencyItem(item.linuxArm64, mav));
                        break;
                    case WINDOWS_ARM:
                        items.add(new DepencyItem(item.windowsArm, mav));
                        break;
                    case LINUX_ARM32:
                        items.add(new DepencyItem(item.linuxArm32, mav));
                        break;
                    case LINUX_LOONGARCH64_OW:
                        items.add(new DepencyItem(item.linuxLoongarch, mav));
                        break;
                }
            } else {
                if (ClassPathInjector.version < 9 && item.old != null) {
                    items.add(new DepencyItem(item.old, mav));
                } else {
                    items.add(new DepencyItem(item.name, mav));
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
            public String maven;
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
