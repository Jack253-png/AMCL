package com.mcreater.amcl.util.fileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {

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
