package com.mcreater.amcl.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtil {
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
                if (outpath.endsWith(".dll") && !new File(outpath).exists()) {
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
