package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.model.mod.ForgeTomlModel;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class ForgeModTomlProcessor implements ModProcessor {
    public List<CommonModInfoModel> process(File file) throws Exception {
        String content = FileUtils.ZipUtil.readTextFileInZip(file.getAbsolutePath(), "META-INF/mods.toml");
        if (content == null) throw new IOException("Not a forge new mod.");
        ForgeTomlModel model = new Toml().read(content).to(ForgeTomlModel.class);

        Vector<CommonModInfoModel> mods = new Vector<>();
        model.mods.forEach(mod -> mods.add(new CommonModInfoModel(
                mod.version,
                mod.displayName,
                mod.description,
                J8Utils.createList(mod.authors),
                file.getAbsolutePath(),
                mod.logoFile,
                mod.displayURL,
                mod.modId
        )));
        return mods;
    }
}
