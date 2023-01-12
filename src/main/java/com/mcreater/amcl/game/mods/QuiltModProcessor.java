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

        String version2 = modInfoModel.quilt_loader.version;
        String name2 = modInfoModel.quilt_loader.metadata.name;
        String description2 = modInfoModel.quilt_loader.metadata.description;
        Vector<String> authorList2 = new Vector<>();
        modInfoModel.quilt_loader.metadata.contributors.keySet().forEach(o -> {
            try {
                authorList2.add((String) o);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        String icon2 = modInfoModel.quilt_loader.metadata.icon;
        String url2 = modInfoModel.quilt_loader.metadata.contact == null ? "" : modInfoModel.quilt_loader.metadata.contact.get("homepage").toString();

        return J8Utils.createList(
                new CommonModInfoModel(
                        version2,
                        name2,
                        description2,
                        authorList2,
                        file.getAbsolutePath(),
                        icon2,
                        url2
                )
        );
    }
}
