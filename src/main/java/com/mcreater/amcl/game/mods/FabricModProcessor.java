package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.FabricModInfoModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class FabricModProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "fabric.mod.json");
        if (content == null) throw new IOException("Not a fabric mod.");
        FabricModInfoModel model;
        try {
            model = GSON_PARSER.fromJson(content, FabricModInfoModel.class);
        } catch (Exception e) {
            throw e;
        }
        String version = model.version;
        String name = model.name;
        String description = model.description;
        Vector<String> ve = new Vector<>();
        if (model.authors != null) {
            for (Object o : model.authors) {
                if (o instanceof String) ve.add((String) o);
                else if (o instanceof Map) {
                    Map map = (Map) o;
                    Object name2 = map.get("name");
                    if (name2 != null) ve.add((String) name2);
                }
            }
        }
        Vector<String> authorList = ve;
        String icon = model.icon;
        String url = model.contact.homepage == null ? "" : model.contact.homepage;
        String modid = model.id;

        return J8Utils.createList(new CommonModInfoModel(version, name, description, authorList, file.getPath(), icon, url, modid));
    }
}
