package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.util.ZipUtil;

import java.io.File;
import java.io.IOException;

public class NativeDownloadTask extends AbstractTask{
    String native_path;
    int chunkSize = 512;
    String hash;
    public NativeDownloadTask(String server, String local, String native_path) {
        super(server, local);
        this.native_path = native_path;
    }
    public NativeDownloadTask(String server, String local, String native_path, int chunkSize) {
        super(server, local);
        this.native_path = native_path;
        this.chunkSize = chunkSize;
    }

    public NativeDownloadTask setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public Integer execute() throws IOException {
        new DownloadTask(server, local, chunkSize).setHash(hash).execute();
        if (new File(native_path).exists()){
            ZipUtil.unzip(local, native_path);
        }
        return null;
    }
}
