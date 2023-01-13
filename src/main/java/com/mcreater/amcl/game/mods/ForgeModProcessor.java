package com.mcreater.amcl.game.mods;

import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.ForgeModInfoModel;
import com.mcreater.amcl.model.mod.OldForgeModInfoModel;
import com.mcreater.amcl.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class ForgeModProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "mcmod.info");
        if (content == null) throw new IOException("Not a forge old mod.");
        if (content.startsWith("[")) {
            Vector<LinkedTreeMap<?, ?>> commonInfo = new Vector<>();
            commonInfo = GSON_PARSER.fromJson(content, commonInfo.getClass());

            Vector<ForgeModInfoModel> cm = new Vector<>();
            commonInfo.forEach(linkedTreeMap -> cm.add(GSON_PARSER.fromJson(GSON_PARSER.toJson(linkedTreeMap), ForgeModInfoModel.class)));

            Vector<CommonModInfoModel> vec = new Vector<>();

            cm.forEach(mi -> vec.add(new CommonModInfoModel(mi.version, mi.name, mi.description, mi.authorList, file.getAbsolutePath(), (mi.modid == null ? "" : mi.modid) + "-logo.png", mi.url, mi.modid)));

            return vec;
        }
        else {
            OldForgeModInfoModel model = GSON_PARSER.fromJson(content, OldForgeModInfoModel.class);
            Vector<CommonModInfoModel> vec = new Vector<>();
            model.modlist.forEach(mu -> vec.add(new CommonModInfoModel(mu.version, mu.name, mu.description, mu.authorList, file.getAbsolutePath(), (mu.modid == null ? "" : mu.modid) + "-logo.png", mu.url, mu.modid)));
            return vec;
        }
    }
}
