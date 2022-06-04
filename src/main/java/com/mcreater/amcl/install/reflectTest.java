package com.mcreater.amcl.install;

import com.mcreater.amcl.download.OriginalDownload;

import java.io.IOException;

public class reflectTest {
    static boolean fast = true;
    public static void main(String[] args) throws IOException {
        OriginalDownload.download(fast, "1.18.2", "D:\\mods\\s\\.minecraft", "1.18.2");
    }
}