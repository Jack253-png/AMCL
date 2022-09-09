package com.mcreater.amcl;

import com.google.gson.Gson;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.patcher.DepenciesLoader;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FileUtils;
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
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Vector;

public class StableMain {
    public static PreLanguageManager manager;
    public static SwingUtils.SplashScreen splashScreen = new SwingUtils.SplashScreen();
    public static SimpleFunctions.Arg0Func<String> getSystem = () -> {
        if (OSInfo.isWin()){
            return "win";
        }
        else if (OSInfo.isLinux()){
            return "linux";
        }
        else if (OSInfo.isMac()){
            return "mac";
        }
        else {
            return "win";
        }
    };
    public static SimpleFunctions.Arg0Func<String> getSystem2 = () -> {
        if (OSInfo.isWin()){
            return "natives-windows";
        }
        else if (OSInfo.isLinux()){
            return "natives-linux";
        }
        else if (OSInfo.isMac()){
            return "natives-macos";
        }
        else {
            return "natives-windows";
        }
    };
    public static void main(String[] args) throws Exception {
        Fonts.loadSwingFont();
        initPreLanguageManager();
        Timer timer = Timer.getInstance();
        try {
            for (File f : Objects.requireNonNull(new File(FileUtils.LinkPath.link(System.getProperty("user.home"), "AppData/Local/JxBrowser")).listFiles())) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }
        catch (Exception ignored){}

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.printf("%s : %s\n", info.getName(), info.getClassName());
        }

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
            Main.main(args);
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
    public static Vector<Task> loadJXBrowserTasks() throws FileNotFoundException {
        Vector<Task> tasks = new Vector<>();
        InputStream s = new ResourceGetter().get("assets/JXBrowserDepency.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(s));
        JXBrowserModel model = new Gson().fromJson(r, JXBrowserModel.class);
        String dir = "AMCL/depencies/JXBrowser";
        new File(dir).mkdirs();
        boolean t = new File(dir, model.getUrl(getSystem.run())).exists();
        if (!t){
            tasks.add(new JXBrowserDownloadTask(model.url, FileUtils.LinkPath.link(dir, "JXBrowser.zip"), model.extracted_files));
        }
        return tasks;
    }
    public static synchronized void downloadJXBrowserJARs() throws FileNotFoundException, InterruptedException {
        TaskManager.addTasks(loadJXBrowserTasks());
        TaskManager.execute("<JXBrowser Download>");
    }
    public static void injectJXBrowserJARs() throws MalformedURLException, InvocationTargetException, IllegalAccessException {
        InputStream s = new ResourceGetter().get("assets/JXBrowserDepency.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(s));
        JXBrowserModel model = new Gson().fromJson(r, JXBrowserModel.class);
        String dir = "AMCL/depencies/JXBrowser";

        ClassPathInjector.addJarUrl(new File(dir, model.getUrl(getSystem.run())).toURI().toURL());
    }
    public static void downloadDepenciesJars(Vector<DepencyItem> addonItems) throws ParserConfigurationException, IOException, InterruptedException, SAXException {
        addonItems.addAll(DepenciesXMLHandler.load());
        Vector<Task> tasks = new Vector<>();
        for (DepencyItem item : addonItems){
            String local = item.getLocal();
            if (!new File(local).exists()) {
                new File(StringUtils.GetFileBaseDir.get(local)).mkdirs();
                tasks.add(new DownloadTask(item.getURL(), local, 2048));
            }
        }
        // disable JXBrowser
//        tasks.addAll(loadJXBrowserTasks());
        DepenciesLoader.checkAndDownload(tasks.toArray(new Task[0]));
        DepenciesLoader.frame.setVisible(false);
        splashScreen.setVisible(true);
    }
    public static void injectDepencies() throws ParserConfigurationException, IOException, SAXException, InvocationTargetException, IllegalAccessException {
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
        // disable JXBrowser
//        injectJXBrowserJARs();
    }
    public static class JXBrowserModel {
        public String url;
        public Vector<String> extracted_files;
        public String win;
        public String linux;
        public String mac;
        public String getUrl(String SystemType){
            if (SystemType.equals("win")){
                return win;
            }
            else if (SystemType.equals("mac")){
                return mac;
            }
            else if (SystemType.equals("linux")){
                return linux;
            }
            else {
                return win;
            }
        }
    }
    public static class JXBrowserDownloadTask extends DownloadTask {
        Vector<String> extractFiles;
        public JXBrowserDownloadTask(String server, String local, Vector<String> extractFiles) throws FileNotFoundException {
            super(server, local);
            this.extractFiles = extractFiles;
        }
        public Integer execute() throws IOException {
            super.execute();
            String dir = "AMCL/depencies/JXBrowser";
            new File(dir).mkdirs();
            FileUtils.ZipUtil.unzipAll(local, dir);
            return 0;
        }
    }
}
