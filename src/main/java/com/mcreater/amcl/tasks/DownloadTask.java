package com.mcreater.amcl.tasks;

import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Objects;

public class DownloadTask extends AbstractTask{
    public int chunkSize = 512;
    String hash;
    HttpURLConnection conn;
    FileOutputStream fos = null;
    InputStream inputStream = null;
    public long downloadBytes;
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
        return !Objects.equals(hash, FileUtils.HashHelper.getFileSHA1(new File(local)));
    }
    public HttpURLConnection getConnection() throws IOException {
        URL url = new URL(this.server);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setConnectTimeout(20000);
        trustAllHosts((HttpsURLConnection) c);
        ((HttpsURLConnection) c).setHostnameVerifier(DO_NOT_VERIFY);
        return c;
    }
    public void clean() throws IOException {
        new File(local).delete();
//        System.gc();
    }
    public void download() throws IOException {
        inputStream = conn.getInputStream();

        new File(StringUtils.GetFileBaseDir.get(this.local)).mkdirs();
        fos = new FileOutputStream(this.local);
        byte[] buffer = new byte[chunkSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            TaskManager.downloadedBytes += chunkSize;
            fos.write(buffer, 0, len);
        }
        conn.disconnect();
    }
    public void d() throws IOException {
        conn = getConnection();
        if (conn.getResponseCode() == 404){
//            throw new Error();
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
            }
            catch (Error e1){
                e1.printStackTrace();
            }
            catch (Exception e) {
                fos.close();
                clean();
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
                    }
                }
            }
            else if (checkHashReverted()){
                execute();
            }
        }
        return null;
    }
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

    /**
     * 信任所有
     */
    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }
}
