package com.mcreater.amcl.download.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

public class OptiFineInstallerDownloadTask extends DownloadTask{
    public OptiFineInstallerDownloadTask(String name, String local) throws FileNotFoundException {
        super(String.format("https://optifine.cn/download/%s", name), local);
    }
    public Integer execute() throws IOException {
        super.execute();
        return null;
    }
}
