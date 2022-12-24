package com.mcreater.amcl.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LibDownloadTask extends DownloadTask{
    public int chunkSize = 512;
    String hash;
    public LibDownloadTask(String server, String local) {
        super(server, local);
    }
    public LibDownloadTask(String server, String local, int chunkSize) throws FileNotFoundException {
        super(server, local);
        this.chunkSize = chunkSize;
    }
    public Integer execute() throws IOException {
        return super.execute();
    }
}
