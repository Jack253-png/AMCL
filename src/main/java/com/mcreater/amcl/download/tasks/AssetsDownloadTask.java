package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.util.FasterUrls;

import java.io.IOException;

public class AssetsDownloadTask extends AbstractTask{
    String hash;
    int chunkSize;
    public AssetsDownloadTask(String hash, String assets_objects_dir, boolean faster) {
        super(FasterUrls.fast(String.format("http://resources.download.minecraft.net/%s/%s", hash.substring(0, 2), hash), faster), String.format("%s\\%s\\%s", assets_objects_dir, hash.substring(0, 2), hash));
    }
    public AssetsDownloadTask(String hash, String assets_objects_dir, boolean faster, int chunkSize) {
        super(FasterUrls.fast(String.format("http://resources.download.minecraft.net/%s/%s", hash.substring(0, 2), hash), faster), String.format("%s\\%s\\%s", assets_objects_dir, hash.substring(0, 2), hash));
        this.chunkSize = chunkSize;
    }
    public AssetsDownloadTask setHash(String hash){
        this.hash = hash;
        return this;
    }
    public Integer execute() throws IOException {
        new DownloadTask(server, local, chunkSize).setHash(hash).execute();
        return null;
    }
}
