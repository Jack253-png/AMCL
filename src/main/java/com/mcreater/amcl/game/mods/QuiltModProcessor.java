package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.QuiltModInfoModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class QuiltModProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "quilt.mod.json");
        QuiltModInfoModel modInfoModel = GSON_PARSER.fromJson(content, QuiltModInfoModel.class);

        String version = modInfoModel.quilt_loader.version;
        String name = modInfoModel.quilt_loader.metadata.name;
        String description = modInfoModel.quilt_loader.metadata.description;
        Vector<String> authorList = new Vector<>();
        modInfoModel.quilt_loader.metadata.contributors.keySet().forEach(o -> {
            try {
                authorList.add((String) o);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        String icon = modInfoModel.quilt_loader.metadata.icon;
        String url = modInfoModel.quilt_loader.metadata.contact == null ? "" : modInfoModel.quilt_loader.metadata.contact.get("homepage").toString();
        String modid = modInfoModel.quilt_loader.id;

        return J8Utils.createList(
                new CommonModInfoModel(
                        version,
                        name,
                        description,
                        authorList,
                        file.getAbsolutePath(),
                        icon,
                        url,
                        modid
                )
        );
    }
}
