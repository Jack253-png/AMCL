package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.util.FasterUrls;
import com.mcreater.amcl.util.HashHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class DownloadTask extends AbstractTask{
    public int chunkSize = 512;
    String hash;
    HttpURLConnection conn;
    FileOutputStream fos = null;
    InputStream inputStream = null;
    public DownloadTask(String server, String local) throws FileNotFoundException {
        super(server, local);
        this.server = this.server.replace("http:", "https:");
        this.server = this.server.replace("maven.modmuss50.me", "maven.fabricmc.net");
    }
    public DownloadTask(String server, String local, int chunkSize) throws FileNotFoundException {
        super(server, local);
        this.server = this.server.replace("http:", "https:");
        this.server = this.server.replace("maven.modmuss50.me", "maven.fabricmc.net");
        this.chunkSize = chunkSize;
    }

    public DownloadTask setHash(String hash) {
        this.hash = hash;
        return this;
    }
    public boolean checkHashReverted(){
        return !Objects.equals(hash, HashHelper.getFileSHA1(new File(local)));
    }
    public HttpURLConnection getConnection() throws IOException {
        URL url = new URL(this.server);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(20000);
        return c;
    }
    public void clean() throws IOException {
        new File(local).delete();
        System.gc();
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
    public void d() throws IOException {
        conn = getConnection();
        if (conn.getResponseCode() == 404){
            server = FasterUrls.rev(server);
            conn = getConnection();
            if (!(conn.getResponseCode() == 404)) {
                download();
            }
            else{
//                throw new IOException();
            }
        }
        else {
            download();
        }
    }
    public Integer execute() throws IOException {
        if ((hash != null) && (checkHashReverted())) {
            clean();
            try {
                d();
            } catch (Exception e) {
                e.printStackTrace();
                fos.close();
                clean();
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1){}
                execute();
            } finally {
                try {
                    fos.close();
                    inputStream.close();
                }
                catch (NullPointerException ignored){
                    return null;
                }
            }
        }
        else{
            if (hash == null){
                while (true){
                    try {
                        clean();
                        d();
                        break;
                    }
                    catch (IOException ignored){
                        ignored.printStackTrace();
                    }
                }
            }
            else if (checkHashReverted()){
                execute();
            }
        }
        return null;
    }
}
