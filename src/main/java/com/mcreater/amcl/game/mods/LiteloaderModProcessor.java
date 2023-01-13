package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.LiteLoaderModInfoModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class LiteloaderModProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "litemod.json");
        LiteLoaderModInfoModel model = GSON_PARSER.fromJson(content, LiteLoaderModInfoModel.class);

        String version = model.revision;
        String name = model.name;
        String description = model.description;
        Vector<String> authorList = new Vector<>(J8Utils.createList(model.author));
        return J8Utils.createList(
                new CommonModInfoModel(version, name, description, authorList, file.getAbsolutePath(), null, "", "")
        );
    }
}
