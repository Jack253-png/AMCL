package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.util.FasterUrls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgeInstallerDownloadTask extends AbstractTask{
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
    public void clean() throws IOException {
        new File(local).delete();
    }
    public void download() throws IOException {
        inputStream = conn.getInputStream();

        //写入到文件
        fos = new FileOutputStream(this.local);
        byte[] buffer = new byte[chunkSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
//            System.out.println(Arrays.toString(buffer));
            fos.write(buffer, 0, len);
        }
        conn.disconnect();
    }
    public Integer execute() throws IOException {
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
            e.printStackTrace();
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
        return null;
    }
}
