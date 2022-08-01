package com.mcreater.amcl.tasks;

import com.mcreater.amcl.util.fileUtils.ZipUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NativeDownloadTask extends DownloadTask{
    String native_path;
    int chunkSize = 512;
    String hash;
    public NativeDownloadTask(String server, String local, String native_path) throws FileNotFoundException {
        super(server, local);
        this.native_path = native_path;
    }
    public NativeDownloadTask(String server, String local, String native_path, int chunkSize) throws FileNotFoundException {
        super(server, local);
        this.native_path = native_path;
        this.chunkSize = chunkSize;
    }

    public Integer execute() throws IOException {
        super.execute();
        if (new File(native_path).exists()){
            ZipUtil.unzip(local, native_path);
        }
        return null;
    }
}
