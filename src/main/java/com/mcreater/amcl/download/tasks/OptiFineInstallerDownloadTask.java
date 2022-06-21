package com.mcreater.amcl.download.tasks;

import java.io.IOException;

public class OptiFineInstallerDownloadTask extends AbstractTask{
    public OptiFineInstallerDownloadTask(String name, String local) {
        super(String.format("https://optifine.cn/download/%s", name), local);
    }
    public Integer execute() throws IOException {
        new DownloadTask(this.server, this.local).execute();
        return null;
    }
}
