package com.mcreater.amcl.tasks;

import com.mcreater.amcl.util.net.FasterUrls;

import java.io.IOException;

import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.net.FasterUrls.ORIGINAL_ASSET_FORMAT;

public class AssetsDownloadTask extends DownloadTask {
    String hash;
    int chunkSize;

    public AssetsDownloadTask(String hash, String assets_objects_dir, FasterUrls.Server server) {
        super(FasterUrls.fast(String.format(ORIGINAL_ASSET_FORMAT, hash.substring(0, 2), hash), server), String.format(buildPath("%s", "%s", "%s"), assets_objects_dir, hash.substring(0, 2), hash));
    }

    public AssetsDownloadTask(String hash, String assets_objects_dir, int chunkSize, FasterUrls.Server server) {
        this(hash, assets_objects_dir, server);
        this.chunkSize = chunkSize;
    }

    public Integer execute() throws IOException {
        return super.execute();
    }
}
