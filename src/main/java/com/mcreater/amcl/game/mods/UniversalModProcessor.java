package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.SimpleModInfoModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class UniversalModProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "pack.mcmeta");
        SimpleModInfoModel model = GSON_PARSER.fromJson(content, SimpleModInfoModel.class);
        String name = model.pack.get("description");
        String description = model.pack.get("description");
        String icon = "logo.png";

        return J8Utils.createList(
                new CommonModInfoModel("", name, description, new Vector<>(), file.getAbsolutePath(), icon, "", "")
        );
    }
}
