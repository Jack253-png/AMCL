package com.mcreater.amcl.lang;

import com.mcreater.amcl.model.mod.transitions.ModTransitionsModel;
import com.mcreater.amcl.natives.ResourceGetter;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

public enum ModTransitions {
    MODS("assets/mods_info_compressed.json");
    private List<ModTransitionsModel> model;
    private final String path;

    ModTransitions(String res) {
        this.path = res;
        model = parse();
    }

    private List<ModTransitionsModel> parse() {
        return JsonUtils.readArray(StringUtils.readFromStream(ResourceGetter.get(path)), ModTransitionsModel.class);
    }

    private Stream<ModTransitionsModel> internalTranslate(String s, String full) {
        return model.parallelStream()
                .filter(model -> {
                    try {
                        if (model.metadata.fabric.modid.equals(s)) return true;
                    } catch (Exception ignored) {
                    }

                    try {
                        if (model.metadata.main.modid.equals(s)) return true;
                    } catch (Exception ignored) {
                    }

                    return model.name.en.contains(full);
                });
    }

    public String translateString(String s, String full) {
        return internalTranslate(s, full)
                .map(modTransitionsModel -> modTransitionsModel.name.cn)
                .findFirst()
                .orElse("");
    }

    public ModTransitionsModel translate(String s, String full) {
        return internalTranslate(s, full)
                .findFirst()
                .orElse(null);
    }
}
