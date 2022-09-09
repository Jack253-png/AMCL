package com.mcreater.amcl.tasks;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.util.net.FasterUrls;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AssetsDownloadTask extends DownloadTask{
    String hash;
    int chunkSize;
    public AssetsDownloadTask(String hash, String assets_objects_dir) throws FileNotFoundException {
        super(FasterUrls.fast(String.format("http://resources.download.minecraft.net/%s/%s", hash.substring(0, 2), hash), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer)), String.format("%s/%s/%s", assets_objects_dir, hash.substring(0, 2), hash));
    }
    public AssetsDownloadTask(String hash, String assets_objects_dir, int chunkSize) throws FileNotFoundException {
        super(FasterUrls.fast(String.format("http://resources.download.minecraft.net/%s/%s", hash.substring(0, 2), hash), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer)), String.format("%s/%s/%s", assets_objects_dir, hash.substring(0, 2), hash));
        this.chunkSize = chunkSize;
    }
    public Integer execute() throws IOException {
        super.execute();
        return null;
    }
}
