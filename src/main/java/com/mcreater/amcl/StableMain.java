package com.mcreater.amcl;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.patcher.DepenciesLoader;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.SimpleFunctions;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.SwingUtils;
import com.mcreater.amcl.util.Timer;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;
import com.mcreater.amcl.util.xml.DepenciesXMLHandler;
import com.mcreater.amcl.util.xml.DepencyItem;
import com.sun.javafx.tk.quantum.QuantumToolkit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.mcreater.amcl.StableMain.JXBrowserDownloadTask.DEFAULT_DIR;

public class StableMain {
    public static PreLanguageManager manager;
    public static SwingUtils.SplashScreen splashScreen = new SwingUtils.SplashScreen();

    public static final String JXBROWSER_URL = "https://storage.googleapis.com/cloud.teamdev.com/downloads/jxbrowser/7.21/jxbrowser-7.21-cross-desktop-win_mac_linux.zip";
    public static SimpleFunctions.Arg0Func<String> getSystem2 = () -> {
        if (OSInfo.isWin()){
            if (OSInfo.isX86()) {
                return "natives-windows-x86";
            }
            return "natives-windows";
        }
        else if (OSInfo.isLinux()){
            return "natives-linux";
        }
        else if (OSInfo.isMac()){
            if (OSInfo.isArm64()){
                return "natives-macos-arm64";
            }
            return "natives-macos";
        }
        else {
            return "natives-windows";
        }
    };
    public static void main(String[] args) throws Exception {
        System.setProperty("log4j.skipJansi", "false");
        System.setProperty("com.sun.webkit.useHTTP2Loader", "false");
        GithubReleases.trustAllHosts();
        Fonts.loadSwingFont();
        initPreLanguageManager();
        Timer timer = Timer.getInstance();

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.printf("%s : %s\n", info.getName(), info.getClassName());
        }

        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        if (ClassPathInjector.version < 11 && !ClassPathInjector.javafx_useable) {
            splashScreen.setVisible(false);
            SwingUtils.showMessage(manager.get("ui.start.failed.title"), manager.get("ui.start.failed.content"), () -> System.exit(1));
        }
        else {
            splashScreen.setVisible(true);
            Vector<DepencyItem> addonItems = new Vector<>();
            downloadDepenciesJars(addonItems);
            injectDepencies();
            fixPulseTimer();
            Logger logger = LogManager.getLogger(StableMain.class);
            logger.info("Initlaze : " + timer.getTimeString());
            Main.start();
        }
    }
    public static void fixPulseTimer(){
        try {
            Field pulseTimer = QuantumToolkit.class.getDeclaredField("pulseTimer");
            pulseTimer.setAccessible(true);
            pulseTimer.set(QuantumToolkit.getToolkit(), new com.sun.glass.ui.Timer(() -> {}){
                protected long _start(Runnable runnable) {return 0;}
                protected long _start(Runnable runnable, int period) {return 0;}
                protected void _stop(long timer) {}
            });
        }
        catch (Throwable e){
            if (ClassPathInjector.version < 11 || ClassPathInjector.version >= 17) e.printStackTrace();
        }
    }
    public static void initPreLanguageManager(){
        manager = new PreLanguageManager(PreLanguageManager.valueOf(LocateHelper.get()));
        manager.initlaze();
    }
    public static void downloadDepenciesJars(Vector<DepencyItem> addonItems) throws Exception {
        addonItems.addAll(DepenciesXMLHandler.load());
        Vector<Task> tasks = new Vector<>();
        for (DepencyItem item : addonItems){
            String local = item.getLocal();
            if (!new File(local).exists()) {
                new File(StringUtils.GetFileBaseDir.get(local)).mkdirs();
                tasks.add(new DownloadTask(item.getURL(), local, 2048));
            }
        }
        Gson gson = new Gson();
        Map<String, String> de = (Map<String, String>) gson.fromJson(new InputStreamReader(ResourceGetter.get("assets/jxbrowser.json")), Map.class);
        String[] sr = new String[]{
                de.get("COMMON"),
                de.get(UICoreType.JAVAFX.toString()),
                de.get(OSInfo.getOSType().toString())
        };

        boolean b = checkJXBrowser(sr);

        DepenciesLoader.checkAndDownload(tasks.toArray(new Task[0]));
        DepenciesLoader.frame.setVisible(false);
        splashScreen.setVisible(true);
    }
    public static void checkJXBrowser2() throws Exception {
        Gson gson = new Gson();
        Map<String, String> de = (Map<String, String>) gson.fromJson(new InputStreamReader(ResourceGetter.get("assets/jxbrowser.json")), Map.class);
        String[] sr = new String[]{
                de.get("COMMON"),
                de.get(UICoreType.JAVAFX.toString()),
                de.get(OSInfo.getOSType().toString())
        };

        boolean b = checkJXBrowser(sr);
        if (!b) {
            Launcher.stage.hide();
            DepenciesLoader.checkAndDownload(new JXBrowserDownloadTask(sr));
            DepenciesLoader.frame.setVisible(false);
            Launcher.stage.show();
        }
    }
    public static boolean checkJXBrowser(String[] arg1) {
        for (String path : arg1) {
            File f = new File(DEFAULT_DIR, path);
            if (!f.exists()) return false;
        }
        for (String path : arg1) {
            File f = new File(DEFAULT_DIR, path);
            try {ClassPathInjector.addJarUrl(f.toURI().toURL());}
            catch (Exception ignored){}
        }
        return true;
    }
    public static void injectDepencies() throws Exception {
        for (DepencyItem item : DepenciesXMLHandler.load()){
            if (item.name.contains("org.openjfx:")){
                if (ClassPathInjector.version >= 11){
                    ClassPathInjector.addJarUrl(new File(item.getLocal()).toURI().toURL());
                }
            }
            else {
                ClassPathInjector.addJarUrl(new File(item.getLocal()).toURI().toURL());
            }
        }
    }

    public static class JXBrowserDownloadTask extends DownloadTask {
        public static final String DEFAULT_PATH = "AMCL/JXBrowser/temp.zip";
        public static final String DEFAULT_DIR = "AMCL/JXBrowser";
        final String[] jars;


        public JXBrowserDownloadTask(String[] jars) {
            super(JXBROWSER_URL, DEFAULT_PATH, 16 * 1024 * 1024);
            new File(DEFAULT_DIR).mkdirs();
            this.jars = jars;
        }
        public Integer execute() throws IOException {
            Integer i = super.execute();
            try (ZipFile file = new ZipFile(DEFAULT_PATH)) {
                for (String s : jars) {
                    ZipEntry entry = file.getEntry(s);
                    if (entry != null) {
                        File f = new File(DEFAULT_DIR, s);
                        f.getParentFile().mkdirs();
                        f.createNewFile();

                        InputStream stream = file.getInputStream(entry);
                        try (OutputStream streamout = Files.newOutputStream(f.toPath())) {
                            byte[] buffer = new byte[2048];
                            int length;
                            while ((length = stream.read(buffer)) > 0) {
                                streamout.write(buffer, 0, length);
                            }
                        }
                        stream.close();
                        ClassPathInjector.addJarUrl(f.toURI().toURL());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }
    }
    public enum UICoreType {
        JAVAFX,
        SWING,
        SWT
    }
}
