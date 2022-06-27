package com.mcreater.amcl.install;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.download.FabricDownload;
import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.download.OptifineDownload;
import com.mcreater.amcl.download.OriginalDownload;
import com.mcreater.amcl.util.ForgeVersionXMLHandler;
import com.mcreater.amcl.util.net.HttpConnectionUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class reflectTest {
    static boolean fast = true;
    public static void main(String[] args) throws IOException, NoSuchFieldException, InterruptedException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        OriginalDownload.download(fast, "inf-20100618", "D:\\mods\\s\\.minecraft", "inf-20100618", 512);
//        ForgeDownload.download(fast, "1.18.2", "D:\\mods\\s\\.minecraft", "1.18.2-forge-40.1.31", 512, "40.1.31");
//        ForgeDownload.download(fast, "1.12.2", "D:\\mods\\s\\.minecraft", "1.12.2-forge-14.23.5.2860", 512, "14.23.5.2860");
//        ForgeDownload.download(fast, "1.12.2", "D:\\mods\\s\\.minecraft", "1.12.2-forge-14.23.5.2812", 1024, "14.23.5.2812");
//        ForgeDownload.download(fast, "1.7.10", "D:\\mods\\s\\.minecraft", "1.7.10-forge-10.13.4.1614", 1024, "10.13.4.1614");
//        FabricDownload.download(fast, "1.16.5", "D:\\mods\\s\\.minecraft", "1.16.5-fabric-0.11.6", 1024, "0.11.6");
//        FabricDownload.download(fast, "1.16.5", "D:\\mods\\s\\.minecraft", "1.16.5-fabric-0.1.0.48", 1024, "0.1.0.48");
//        FabricDownload.download(fast, "1.19", "D:\\mods\\s\\.minecraft", "1.19-fabric-0.14.7", 1024, "0.14.7");
//        OriginalDownload.download(fast, "1.19", "D:\\mods\\s\\.minecraft", "1.19", 512);
//        OptifineDownload.download(fast, "1.12.2", "D:\\mods\\s\\.minecraft", "1.12.2-optitest", 1024, "HD_U_G5");
        OptifineDownload.download(fast, "1.7.10", "D:\\mods\\s\\.minecraft", "1.7.10-optitest", 1024, "HD_U_E7");
    }
}