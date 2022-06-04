package com.mcreater.amcl.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    public static void unzipAll(String iy, String o){
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

                OutputStream out = new FileOutputStream(outpath);
                byte[] bf = new byte[4096];
                int len;
                while ((len = in.read(bf)) > 0) {
                    out.write(bf, 0, len);
                }
                in.close();
                out.close();
            }
            zp.close();
        }catch ( Exception e){
            e.printStackTrace();
        }
    }
}
