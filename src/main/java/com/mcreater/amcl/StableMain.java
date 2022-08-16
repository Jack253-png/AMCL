package com.mcreater.amcl;

import com.google.gson.Gson;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.patcher.depenciesLoader;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;
import com.mcreater.amcl.util.xml.DepenciesXMLHandler;
import com.mcreater.amcl.util.xml.DepencyItem;
import com.sun.javafx.tk.quantum.QuantumToolkit;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Vector;

public class StableMain {
    public static PreLanguageManager manager;
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        try {
            for (File f : Objects.requireNonNull(new File(FileUtils.LinkPath.link(System.getProperty("user.home"), "AppData\\Local\\JxBrowser")).listFiles())) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }
        catch (Exception ignored){}
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (ClassPathInjector.version < 11 && !ClassPathInjector.javafx_useable){
            JOptionPane.showMessageDialog(null, "launcher cannot fix javafx environment in java 8-10", "javafx broken", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        fixPulseTimer();
        initPreLanguageManager();
        Vector<DepencyItem> addonItems = new Vector<>();
        downloadDepenciesJars(addonItems);
        injectDepencies();
        Main.main(args);
    }
    public static void fixPulseTimer(){
        try {
            Field f = QuantumToolkit.class.getDeclaredField("pulseTimer");
            f.setAccessible(true);
            f.set(QuantumToolkit.getToolkit(), new com.sun.glass.ui.Timer(() -> {}) {
                protected long _start(Runnable runnable) {
                    return 0;
                }

                protected long _start(Runnable runnable, int period) {
                    return 0;
                }

                protected void _stop(long timer) {

                }
            });
        }
        catch (Throwable ignored){}
    }
    public static void initPreLanguageManager(){
        manager = new PreLanguageManager(PreLanguageManager.valueOf(LocateHelper.get()));
        manager.initlaze();
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
        InputStream s = new ResourceGetter().get("assets/JXBrowserDepency.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(s));
        JXBrowserModel model = new Gson().fromJson(r, JXBrowserModel.class);
        String dir = "AMCL/depencies/JXBrowser";
        new File(dir).mkdirs();
        boolean t = true;
        for (String p : model.extracted_files){
            if (!new File(dir, p).exists()){
                t = false;
                break;
            }
        }
        if (!t){
            tasks.add(new JXBrowserDownloadTask(model.url, FileUtils.LinkPath.link(dir, "JXBrowser.zip"), model.extracted_files));
        }
        depenciesLoader.checkAndDownload(tasks.toArray(new Task[0]));
        depenciesLoader.frame.setVisible(false);
    }
    public static void injectDepencies() throws ParserConfigurationException, IOException, SAXException, InvocationTargetException, IllegalAccessException {
        for (DepencyItem item : DepenciesXMLHandler.load()){
            ClassPathInjector.addJarUrl(new File(item.getLocal()).toURI().toURL());
        }
        InputStream s = new ResourceGetter().get("assets/JXBrowserDepency.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(s));
        JXBrowserModel model = new Gson().fromJson(r, JXBrowserModel.class);
        String dir = "AMCL/depencies/JXBrowser";
        for (String p : model.extracted_files){
            ClassPathInjector.addJarUrl(new File(dir, p).toURI().toURL());
        }
    }
    public static class JXBrowserModel {
        public String url;
        public Vector<String> extracted_files;
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
            return null;
        }
    }
}
