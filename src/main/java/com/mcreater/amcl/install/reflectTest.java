package com.mcreater.amcl.install;

import com.mcreater.amcl.download.*;
import com.mcreater.amcl.game.launch.MinecraftFixer;
import org.xml.sax.SAXException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class reflectTest {
    static boolean fast = true;
    public static void main(String[] args) throws IOException, NoSuchFieldException, InterruptedException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ParserConfigurationException, SAXException {
        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
//        OriginalDownload.download(fast, "inf-20100618", "D:\\mods\\s\\.minecraft", "inf-20100618", 512);
//        ForgeDownload.download(fast, "1.19", "D:\\mods\\test2\\.minecraft", "1.19-forge-41.0.1", 4096, "41.0.1");
//        ForgeDownload.download(fast, "1.18.2", "D:\\mods\\test2\\.minecraft", "1.18.2-forge-40.1.31", 4096, "40.1.0");
//        MinecraftFixer.fix(fast, 4096, "D:\\mods\\test2\\.minecraft", "1.16.5-forge-36.2.20");
//        ForgeDownload.download(fast, "1.16.5", "D:\\mods\\test2\\.minecraft", "1.16.5-forge-36.2.20", 4096, "36.2.20");
//        ForgeDownload.download(fast, "1.14.4", "D:\\mods\\test2\\.minecraft", "1.14.4-forge-28.2.26", 4096, "28.2.26");
//        ForgeDownload.download(fast, "1.12.2", "D:\\mods\\test2\\.minecraft", "1.12.2-forge-14.23.5.2860", 4096, "14.23.5.2860");
//        ForgeDownload.download(fast, "1.12.2", "D:\\mods\\testDownload\\.minecraft", "1.12.2-forge-old", 1024, "14.23.5.2812");
//        ForgeDownload.download(fast, "1.7.10", "D:\\mods\\test2\\.minecraft", "1.7.10-forge-10.13.4.1614", 4096, "10.13.4.1614");
//        FabricDownload.download(fast, "1.16.5", "D:\\mods\\testDownload\\.minecraft", "1.16.5-fabric-0.11.6", 1024, "0.11.6");
//        FabricDownload.download(fast, "1.16.5", "D:\\mods\\s\\.minecraft", "1.16.5-fabric-0.1.0.48", 1024, "0.1.0.48");
//        FabricDownload.download(fast, "1.19", "D:\\mods\\s\\.minecraft", "1.19-fabric-0.14.7", 1024, "0.14.7");
//        OriginalDownload.download(fast, "1.19", "D:\\mods\\s\\.minecraft", "1.19", 512);
//        OptifineDownload.download(fast, "1.12.2", "D:\\mods\\test2\\.minecraft", "1.12.2-optitest", 4096, "HD_U_G5");
//        OptifineDownload.download(fast, "1.7.10", "D:\\mods\\s\\.minecraft", "1.7.10-optitest", 1024, "HD_U_E7");
//        OptifineDownload.download(fast, "1.5.2", "D:\\mods\\testDownload\\.minecraft", "1.5.2-optitest", 1024, "HD_U_D5");
//        OriginalDownload.download(fast, "1.19.1-pre4", "D:\\mods\\test2\\.minecraft", "1.19.1-pre4", 4096);


//        OptifineDownload.download(fast, "1.19", "D:\\mods\\test2\\.minecraft", "1.19-optitest", 4096, "HD_U_H8_pre10");
//        OptifineDownload.download(fast, "1.16.5", "D:\\mods\\test2\\.minecraft", "1.16.5-optitest", 4096, "HD_U_G8_pre9");
//        ForgeDownload.download(fast, "1.1", "D:\\\\mods\\\\test2\\\\.minecraft", "1.1-forge-1.3.2.1", 4096, "1.3.2.1");


//        GetVersionList.getOriginalList(fast);
//        GetVersionList.getForgeVersionList(fast, "1.16.5");
//        GetVersionList.getFabricVersionList(fast, "1.16.5");
//        GetVersionList.getOptifineVersionList(fast, "1.16.5");
//        GetVersionList.getFabricAPIVersionList(fast, "1.16.5");
//        GetVersionList.getOptiFabricVersionList(fast, "1.16.5");
    }
}