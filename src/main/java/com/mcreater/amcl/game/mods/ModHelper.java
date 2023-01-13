package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.model.mod.CommonModInfoModel;
import com.mcreater.amcl.util.FileUtils.LinkPath;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class ModHelper {
    public static Vector<File> getMod(String dir, String version_name){
        Vector<File> result = new Vector<>();
        boolean changed = Launcher.configReader.configModel.change_game_dir;
        File d = new File(changed ? LinkPath.link(LinkPath.link(LinkPath.link(dir, "versions"), version_name), "mods") : LinkPath.link(dir, "mods"));
        if (d.exists()) {
            for (File f : d.listFiles()) {
                if (f.isFile()) {
                    if (f.getPath().endsWith(".jar") || f.getPath().endsWith(".litemod") || f.getPath().endsWith(".zip")) {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }
    public static List<CommonModInfoModel> getModInfo(File file) {
        try {
            return ModProcessor.getForgeModTomlProcessor().process(file);
        }
        catch (Exception ignored) {}

        try {
            return ModProcessor.getForgeModProcessor().process(file);
        }
        catch (Exception ignored) {}

        try {
            return ModProcessor.getFabricModProcessor().process(file);
        }
        catch (Exception ignored) {}

        try {
            return ModProcessor.getQuiltModProcessor().process(file);
        }
        catch (Exception ignored) {}

        try {
            return ModProcessor.getLiteloaderModProcessor().process(file);
        }
        catch (Exception ignored) {}

        try {
            return ModProcessor.getUniversalModProcessor().process(file);
        }
        catch (Exception ignored) {}

        return new Vector<>();
    }
}
