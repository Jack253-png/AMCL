package com.mcreater.amcl.game.mods;

import com.mcreater.amcl.model.mod.CommonModInfoModel;

import java.io.File;
import java.util.List;

public interface ModProcessor {
    List<CommonModInfoModel> process(File file) throws Exception;
    static ModProcessor getFabricModProcessor() {
        return new FabricModProcessor();
    }
    static ModProcessor getForgeModProcessor() {
        return new ForgeModProcessor();
    }
    static ModProcessor getQuiltModProcessor() {
        return new QuiltModProcessor();
    }
    static ModProcessor getLiteloaderModProcessor () {
        return new LiteloaderModProcessor();
    }
    static ModProcessor getUniversalModProcessor () {
        return new UniversalModProcessor();
    }
}
