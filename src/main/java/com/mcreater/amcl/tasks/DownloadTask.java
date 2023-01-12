package com.mcreater.amcl.tasks;

import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.net.FasterUrls;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.deleteFile;

public class DownloadTask extends AbstractDownloadTask {
    public int chunkSize = 512;
    String hash;
    HttpURLConnection conn;
    FileOutputStream fos = null;
    InputStream inputStream = null;

    public DownloadTask(String server, String local) {
        super(server, local);
        this.server = this.server.replace("http:", "https:");
        this.server = this.server.replace("maven.modmuss50.me", "maven.fabricmc.net");
    }
    public DownloadTask(String server, String local, int chunkSize) {
        super(server, local);
        this.server = this.server.replace("http:", "https:");
        this.server = this.server.replace("maven.modmuss50.me", "maven.fabricmc.net");
        this.chunkSize = chunkSize;
    }

    public DownloadTask setHash(String hash) {
        this.hash = hash;
        return this;
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
        deleteFile(local);
    }
    public void download() throws IOException {
        inputStream = conn.getInputStream();

        createDirectory(this.local);
        clean();
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
        if (server.contains("optifine.cn")) {
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Mobile Safari/537.36 Edg/104.0.1293.54");
            conn.setRequestProperty("Content-Type", "application/json");
        }

        if (conn.getResponseCode() == 404){
            server = FasterUrls.ReturnToOriginServer(server);
            conn = getConnection();
            if (!(conn.getResponseCode() == 404)) {
                download();
            }
        }
        else {
            download();
        }
    }
    public Integer execute() throws IOException {
        boolean failVail = FileUtils.HashHelper.validateSHA1(new File(local), hash);
        if (!failVail) {
            clean();
            try {
                d();
            }
            catch (Exception e) {
                J8Utils.runSafe(() -> fos.close());
                clean();
                execute();
            } finally {
                J8Utils.runSafe(
                        () -> fos.close(),
                        () -> inputStream.close()
                );
            }
        }
        else if (hash == null) {
            while (true){
                try {
                    clean();
                    d();
                    break;
                }
                catch (IOException ignored){}
            }
        }
        return null;
    }
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
    }};
    private static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;
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
