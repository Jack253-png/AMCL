package com.mcreater.amcl;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.mcreater.amcl.api.githubApi.GithubReleases;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.patcher.DepenciesLoader;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.LoggerPrintStream;
import com.mcreater.amcl.util.SimpleFunctions;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.SwingUtils;
import com.mcreater.amcl.util.Timer;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;
import com.mcreater.amcl.util.parsers.DepenciesJsonHandler;
import com.mcreater.amcl.util.parsers.DepencyItem;
import com.sun.javafx.tk.quantum.QuantumToolkit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;

public class StableMain {
    public static PreLanguageManager manager;
    public static SwingUtils.SplashScreen splashScreen = new SwingUtils.SplashScreen();

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
        LoggerPrintStream.setStdStream();
        System.setProperty("log4j.skipJansi", "false");

        GithubReleases.trustAllHosts();
        Fonts.loadSwingFont();
        initPreLanguageManager();
        Timer timer = Timer.getInstance();

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
            ClassPathInjector.checkJavaFXState();
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
    }
    public static void downloadDepenciesJars(Vector<DepencyItem> addonItems) throws Exception {
        addonItems.addAll(DepenciesJsonHandler.load());
        Vector<Task> tasks = new Vector<>();
        for (DepencyItem item : addonItems){
            String local = item.getLocal();
            if (!new File(local).exists()) {
                createDirectory(local);
                tasks.add(new DownloadTask(item.getURL(), local, 2048));
            }
        }

        DepenciesLoader.checkAndDownload(tasks.toArray(new Task[0]));
        DepenciesLoader.frame.setVisible(false);
        splashScreen.setVisible(true);
    }
    public static void injectDepencies() throws Exception {
        for (DepencyItem item : DepenciesJsonHandler.load()){
            if (item.name.contains("org.openjfx:")){
                if (!ClassPathInjector.javafx_useable){
                    ClassPathInjector.addJarUrl(new File(item.getLocal()).toURI().toURL());
                }
            }
            else {
                ClassPathInjector.addJarUrl(new File(item.getLocal()).toURI().toURL());
            }
        }
    }
}
