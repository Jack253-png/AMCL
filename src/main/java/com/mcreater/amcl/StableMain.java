package com.mcreater.amcl;

import com.google.gson.Gson;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.patcher.depenciesLoader;
import com.mcreater.amcl.tasks.AbstractTask;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.operatingSystem.LocateHelper;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.xml.DepenciesXMLHandler;
import com.mcreater.amcl.util.xml.DepencyItem;
import com.mcreater.amclAPI.Plugin;
import com.mcreater.amclAPI.Version;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class StableMain {
    static Logger logger = LogManager.getLogger(StableMain.class);
    public static PreLanguageManager manager;
    static Vector<String> intros = new Vector<>();
    public static Vector<Pair<Plugin, Version>> plugins = new Vector<>();
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        initPreLanguageManager();
        File plugins = getPluginDir();
        intros = new Vector<>();
        Vector<DepencyItem> addonItems = new Vector<>();
        handlePluginJar(plugins, intros, addonItems);
        downloadDepenciesJars(addonItems);
        injectDepencies();
        Main.main(args);
    }
    public static File getPluginDir(){
        File plugins = new File("AMCL/plugins");
        plugins.mkdirs();
        return plugins;
    }
    public static void initPreLanguageManager(){
        manager = new PreLanguageManager(PreLanguageManager.valueOf(LocateHelper.get()));
        manager.initlaze();
    }
    public static Method getMethod(Object ucp) throws NoSuchMethodException {
        Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        return method;
    }
    public static Object getUCP() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("jdk.internal.loader.BuiltinClassLoader").getDeclaredField("ucp");
        field.setAccessible(true);
        return field.get(ClassLoader.getSystemClassLoader());
    }
    public static void handlePluginJar(File plugins, final Vector<String> intros, final Vector<DepencyItem> addonItems) throws IOException, ParserConfigurationException, SAXException, InvocationTargetException, IllegalAccessException {
        for (File f : plugins.listFiles()){
            if (!f.getPath().endsWith(".jar")){
                continue;
            }
            ClassPathInjector.addJarUrl(f.toURI().toURL());
            try (JarFile jarFile1 = new JarFile(f)) {
                Attributes attrs = jarFile1.getManifest().getMainAttributes();
                for (Object o : attrs.keySet()) {
                    Attributes.Name attrName = (Attributes.Name) o;
                    if (Objects.equals(attrName.toString(), "Main-Class")) {
                        intros.add((String) attrs.get(attrName));
                    }
                }
                addonItems.addAll(loadPluginDepencies(f));
            }
        }
    }
    public static Vector<DepencyItem> loadPluginDepencies(File f) throws IOException, ParserConfigurationException, SAXException {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{f.toURI().toURL()})) {
            return DepenciesXMLHandler.load(loader.findResource("assets/depencies.xml").openStream());
        }
        catch (NullPointerException e){
            return new Vector<>();
        }
    }
    public static void downloadDepenciesJars(Vector<DepencyItem> addonItems) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException {
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
    public static void injectDepencies() throws ParserConfigurationException, IOException, SAXException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
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
        for (String main : intros){
            Class<?> clazz = Class.forName(main);
            Version version = clazz.getAnnotation(Version.class);
            Plugin name = clazz.getAnnotation(Plugin.class);
            plugins.add(new Pair<>(name, version));
        }
    }
    public static boolean checkVersionComptiable(String versions){
        if (versions.equals("ALL-VERSION") || versions.equals(VersionInfo.launcher_version)){
            return true;
        }
        List<String> vers = List.of(versions.split(";"));
        return vers.contains(VersionInfo.launcher_version);
    }
    public static void initPlugins(Vector<String> mainClasses, String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        for (String main : mainClasses){
            Class<?> clazz = Class.forName(main);
            Version version = clazz.getAnnotation(Version.class);
            Plugin name = clazz.getAnnotation(Plugin.class);
            if (version != null && name != null){
                if (checkVersionComptiable(version.version())){
                    try {
                        clazz.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
                        logger.info(String.format("inited plugin \"%s\" version %s", name.name(), name.version()));
                    }
                    catch (InvocationTargetException e){
                        logger.error(String.format("error to init plugin \"%s\" version %s", name.name(), name.version()), e);
                        setFailed(name);
                    }
                }
                else{
                    logger.warn(String.format("plugin \"%s\" needed launcher version %s, but now it's %s", name.name(), version.version(), VersionInfo.launcher_version));
                    setFailed(name);
                }
            }
            else{
                logger.warn(String.format("plugin with mainClass %s with wrong format", main));
            }
        }
    }
    private static void setFailed(Plugin name){
        plugins.forEach(e -> {
            if (e.getKey() == name){
                name.loadSuccessed.set(false);
            }
        });
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
