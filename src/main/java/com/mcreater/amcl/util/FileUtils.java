package com.mcreater.amcl.util;

import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.nativeInterface.PosixHandler;
import com.mcreater.amcl.patcher.ClassPathInjector;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public static class OperateUtil {
        public static void deleteFile(String path) {
            try {
                Path path2 = Paths.get(path);
                Files.deleteIfExists(path2);
            }
            catch (Exception ignored) {

            }
        }
        public static void deleteDirectory(File f, String orgin){
            if (!f.exists()){
                return;
            }
            if (f.isFile()){
                f.delete();
                return;
            }
            else {
                for (File f1 : f.listFiles()){
                    deleteDirectory(f1, orgin);
                }
            }
            if (!f.getPath().equals(orgin)) {
                f.delete();
            }
        }
        public static void createDirectory(String path) {
            new File(StringUtils.GetFileBaseDir.get(path)).mkdirs();
        }
        public static void createDirectoryDirect(String path) {
            new File(path).mkdirs();
        }
    }
    public static String getJavaExecutable() {
        String env = getJavaExecutableInEnv();
        if (env != null) return env;
        Vector<File> path = getJavaExecutableInPath();
        if (path.size() > 0) {
            return path.get(0).getAbsolutePath();
        }
        return getCurrentJavaExecutable();
    }
    public static String getCurrentJavaExecutable() {
        String home = System.getProperty("java.home");
        if (OSInfo.isMac()) {
            home = LinkPath.link(home, "Contents/Home/bin/java");
        }
        else if (OSInfo.isLinux()) {
            home = LinkPath.link(home, "bin/java");
        }
        else {
            home = LinkPath.link(home, "bin/java.exe");
        }
        try {
            if (ClassPathInjector.getJavaVersion(new File(home)) >= 8) {
                return home;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Vector<File> getJavaExecutableInPath() {
        String[] arg = System.getenv("Path").split(File.pathSeparator);
        Vector<Thread> threads = new Vector<>();
        CountDownLatch latch = new CountDownLatch(arg.length);
        Vector<File> paths = new Vector<>();
        for (String p : arg) {
            threads.add(new Thread(() -> {
                try {
                    File[] files = new File(p).listFiles((dir, name) -> name.contains(OSInfo.isWin() ? "java.exe" : "java"));
                    if (files != null) {
                        for (File f : files) {
                            if (ClassPathInjector.getJavaVersion(f) >= 8) {
                                paths.add(f);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }));
        }
        threads.forEach(Thread::start);
        try {latch.await();}
        catch (Exception ignored) {}

        return paths;
    }
    private static String getJavaExecutableInEnv() {
        try {
            File envPath = new File(System.getenv("JAVA_HOME"), OSInfo.isWin() ? "java.exe" : "java");
            if (ClassPathInjector.getJavaVersion(envPath) >= 8) return envPath.getPath();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static class ChangeDir {
        public static String dirs;

        public static void saveNowDir(){
            dirs = System.getProperty("user.dir");
        }
        public static void changeToDefault(){
            changeTo(dirs);
        }
        public static void changeTo(String dir){
            System.setProperty("user.dir", dir);
            PosixHandler handler = new PosixHandler();
            handler.setVerbose(true);
            POSIX posix = POSIXFactory.getPOSIX(handler, true);
            posix.chdir(dir);
        }
    }
    public static class FileStringReader {
        public static String read(String p){
            File file = new File(p.replace("\\", "/"));
            BufferedReader reader = null;
            StringBuilder r = new StringBuilder();
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    // 显示行号
                    r.append(tempString).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Null to read file");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return r.toString();
        }
    }
    public static void del(String p){
        Path path = Paths.get(p);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException ignored){

        }
    }
    public static class HashHelper {
        public static boolean validateSHA1(File file, String target) {
            if (!file.exists()) {
                return false;
            }
            if (target == null) {
                return true;
            }
            String fileSHA1 = getFileSHA1(file);
            return Objects.equals(fileSHA1, target);
        }
        private static String getFileSHA1(File file) {
            MessageDigest md;
            FileInputStream fis = null;
            StringBuilder sha1Str = new StringBuilder();
            try {
                fis = new FileInputStream(file);
                MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                md = MessageDigest.getInstance("SHA-1");
                md.update(mbb);
                byte[] digest = md.digest();
                String shaHex = "";
                for (byte b : digest) {
                    shaHex = Integer.toHexString(b & 0xFF);
                    if (shaHex.length() < 2) {
                        sha1Str.append(0);
                    }
                    sha1Str.append(shaHex);
                }
            } catch (NoSuchAlgorithmException | IOException e) {
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return sha1Str.toString();

        }
    }
    public static class LinkPath {
        public static String link(String p1,String p2){
            return rep(new File(p1, p2).getPath());
        }
        public static String rep(String p){
            return p.replace("\\", "/");
        }
    }
    public static class RemoveFileToTrash {
        public static void remove(String path){
            File f = new File(path);
            if (f.exists()) {
                com.sun.jna.platform.FileUtils fu = com.sun.jna.platform.FileUtils.getInstance();
                if (fu.hasTrash()) {
                    try {
                        fu.moveToTrash(f);
                    } catch (IOException e) {
                        f.delete();
                    }
                } else {
                    f.delete();
                }
            }
        }
    }
    public static class ZipUtil {
        public static Map<String, String> getNativeHash(String iy) throws IOException {
            Map<String, String> haMap = new HashMap<>();
            ZipFile zp = new ZipFile(iy, Charset.forName("gbk"));
            Enumeration<? extends ZipEntry> entries = zp.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".sha1")) {
                    InputStream stream = zp.getInputStream(entry);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                    String s = reader.readLine();
                    reader.close();
                    haMap.put(entry.getName().replace(".sha1", ""), s);
                }
            }
            zp.close();
            return haMap;
        }
        public static void unzip(String iy,String o) {
            Map<String, String> haMap = new HashMap<>();
            try {
                haMap = getNativeHash(iy);
            }
            catch (Exception e) {}

            File pathFile = new File(o);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            ZipFile zp;
            try {
                zp = new ZipFile(iy, Charset.forName("gbk"));
                Enumeration<? extends ZipEntry> entries = zp.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String zipEntryName = entry.getName();
                    InputStream in = zp.getInputStream(entry);
                    String outpath = (LinkPath.link(o, zipEntryName)).replace("/",File.separator);
                    File file = new File(outpath.substring(0,outpath.lastIndexOf(File.separator)));
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (new File(outpath).isDirectory())
                        continue;
                    boolean isPassed = HashHelper.validateSHA1(new File(outpath), haMap.get(new File(outpath).getName()));
                    if ((outpath.endsWith(".dll") || outpath.endsWith(".so") || outpath.endsWith(".dylib")) && !isPassed) {
                        OutputStream out = Files.newOutputStream(Paths.get(outpath));
                        byte[] bf = new byte[4096];
                        int len;
                        while ((len = in.read(bf)) > 0) {
                            out.write(bf, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
                zp.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void unzipAll(String iy, String o) throws IOException {
            File desDir = new File(o);
            if (!desDir.exists()) {
                boolean mkdirSuccess = desDir.mkdir();
            }
            // 读入流
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(iy)));
            // 遍历每一个文件
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) { // 文件夹
                    String unzipFilePath = o + File.separator + zipEntry.getName();
                    // 直接创建
                    mkdir(new File(unzipFilePath));
                } else { // 文件
                    String unzipFilePath = o + File.separator + zipEntry.getName();
                    File file = new File(unzipFilePath);
                    // 创建父目录
                    mkdir(file.getParentFile());
                    // 写出文件流
                    BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                    byte[] bytes = new byte[1024];
                    int readLen;
                    while ((readLen = zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, readLen);
                    }
                    bufferedOutputStream.close();
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        }
        private static void mkdir(File file) {
            if (null == file || file.exists()) {
                return;
            }
            mkdir(file.getParentFile());
            file.mkdir();
        }
        public static String readTextFileInZip(String zipFile, String internalFileName) throws IOException {
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            String content = "";
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().equals(internalFileName)) {
                    Vector<Byte> b = new Vector<>();
                    byte[] bytes = new byte[1];
                    while (zipInputStream.read(bytes) != -1) {
                        b.addAll(J8Utils.createList(bytes[0]));
                    }
                    Byte[] array = b.toArray(new Byte[0]);
                    byte[] finalData = new byte[array.length];
                    for (int index = 0;index < array.length;index++){
                        finalData[index] = array[index];
                    }

                    content = new String(finalData);
                    zipInputStream.closeEntry();
                    return content;
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return null;
        }
    }
}
