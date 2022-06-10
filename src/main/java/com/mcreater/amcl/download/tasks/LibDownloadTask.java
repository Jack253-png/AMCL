package com.mcreater.amcl.download.tasks;

import java.io.IOException;

public class LibDownloadTask extends AbstractTask{
    public int chunkSize = 512;
    String hash;
    public LibDownloadTask(String server, String local) {
        super(server, local);
    }
    public LibDownloadTask(String server, String local, int chunkSize) {
        super(server, local);
        this.chunkSize = chunkSize;
    }
    public LibDownloadTask setHash(String hash){
        this.hash = hash;
        return this;
    }
    public Integer execute() throws IOException {
        new DownloadTask(server, local, chunkSize).setHash(hash).execute();
        return null;
    }
}
