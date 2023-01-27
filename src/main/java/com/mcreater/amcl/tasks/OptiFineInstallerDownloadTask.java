package com.mcreater.amcl.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OptiFineInstallerDownloadTask extends DownloadTask {
    public OptiFineInstallerDownloadTask(String name, String local) {
        super(String.format("https://optifine.cn/download/%s", name), local);
        try {
            new File(this.local).createNewFile();
        } catch (Exception e) {
        }
    }

    public Integer execute() throws IOException {
        return super.execute();
    }
}
