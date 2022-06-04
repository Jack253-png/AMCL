package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.HashHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class DownloadTask extends AbstractTask{
    public int chunkSize = 512;
    String hash;
    HttpURLConnection conn;
    FileOutputStream fos = null;
    InputStream inputStream = null;
    public DownloadTask(String server, String local) {
        super(server, local);
    }
    public DownloadTask(String server, String local, int chunkSize) {
        super(server, local);
        this.chunkSize = chunkSize;
    }

    public DownloadTask setHash(String hash) {
        this.hash = hash;
        return this;
    }
    public boolean checkHash(){
        return Objects.equals(hash, HashHelper.getFileSHA1(new File(local)));
    }
    public HttpURLConnection getConnection() throws IOException {
        URL url = new URL(this.server);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(15000);
        return c;
    }
    public void clean() throws IOException {
        BufferedWriter w = new BufferedWriter(new FileWriter(local));
        w.write("");
        w.close();
    }
    public void download() throws IOException {
        inputStream = conn.getInputStream();

        //写入到文件
        fos = new FileOutputStream(this.local);
        byte[] buffer = new byte[chunkSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        conn.disconnect();
    }
    public void execute() throws IOException {
        if ((hash != null) && (!checkHash())) {
            clean();
            try {
                // 包含中文字符时需要转码
                conn = getConnection();
                if (conn.getResponseCode() == 404){
                    server = FasterUrls.rev(server);
                    conn = getConnection();
                    if (!(conn.getResponseCode() == 404)) {
                        download();
                    }
                }
                else {
                    download();
                }
            } catch (Exception e) {
                clean();
                execute();
            } finally {
                try {
                    fos.close();
                    inputStream.close();
                }
                catch (NullPointerException ignored){
                }
            }
        }
    }
}
