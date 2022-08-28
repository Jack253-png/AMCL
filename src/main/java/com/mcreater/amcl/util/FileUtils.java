package com.mcreater.amcl.util;

import com.mcreater.amcl.nativeInterface.PosixHandler;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils {
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
            File file = new File(p);
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
        public static String getFileSHA1(File file) {
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
            } catch (NoSuchAlgorithmException | IOException ignored) {
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
            return p.replace("/",File.separator)
                    .replace("\\", File.separator);
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
        public static void unzip(String iy,String o) throws IOException {
            File pathFile=new File(o);
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }
            ZipFile zp=null;
            try{
                zp=new ZipFile(iy, Charset.forName("gbk"));
                Enumeration<? extends ZipEntry> entries=zp.entries();
                while (entries.hasMoreElements()){
                    ZipEntry entry= entries.nextElement();
                    String zipEntryName = entry.getName();
                    InputStream in = zp.getInputStream(entry);
                    String outpath=(LinkPath.link(o, zipEntryName)).replace("/",File.separator);
                    File file = new File(outpath.substring(0,outpath.lastIndexOf(File.separator)));
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    if(new File(outpath).isDirectory())
                        continue;
                    if ((outpath.endsWith(".dll") || outpath.endsWith(".so")) && !new File(outpath).exists()) {
                        OutputStream out = new FileOutputStream(outpath);
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
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
        public static void unzipAll(String iy, String o) throws IOException {
            File desDir = new File(o);
            if (!desDir.exists()) {
                boolean mkdirSuccess = desDir.mkdir();
            }
            // 读入流
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(iy));
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
                            new BufferedOutputStream(new FileOutputStream(unzipFilePath));
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
    }
}
