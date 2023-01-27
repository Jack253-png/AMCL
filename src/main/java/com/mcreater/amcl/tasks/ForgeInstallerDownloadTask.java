package com.mcreater.amcl.tasks;

import com.mcreater.amcl.util.net.FasterUrls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteFile;

public class ForgeInstallerDownloadTask extends AbstractDownloadTask {
    FileOutputStream fos = null;
    InputStream inputStream = null;
    HttpURLConnection conn;
    int chunkSize = 512;

    public ForgeInstallerDownloadTask(String server, String local) {
        super(server, local);
    }

    public ForgeInstallerDownloadTask(String server, String local, int chunkSize) {
        super(server, local);
        this.chunkSize = chunkSize;
    }

    public HttpURLConnection getConnection() throws IOException {
        URL url = new URL(this.server);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(15000);
        return c;
    }

    public void clean() {
        deleteFile(local);
    }

    public void download() throws IOException {
        inputStream = conn.getInputStream();

        fos = new FileOutputStream(this.local);
        byte[] buffer = new byte[chunkSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        conn.disconnect();
    }

    public Integer execute() throws IOException {
        clean();
        try {
            conn = getConnection();
            if (conn.getResponseCode() >= 300) {
                server = FasterUrls.ReturnToOriginServer(server, TaskType.FORGE);
                conn = getConnection();
                if (!(conn.getResponseCode() >= 300)) {
                    download();
                }
            } else {
                download();
            }
        } catch (Exception e) {
            e.printStackTrace();
            clean();
            execute();
        } finally {
            try {
                fos.close();
                inputStream.close();
            } catch (NullPointerException ignored) {
            }
        }
        return null;
    }
}
