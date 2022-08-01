package com.mcreater.amcl;

import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.patcher.depenciesLoader;
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
import java.io.File;
import java.io.IOException;
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
    static Vector<String> intros;
    public static Vector<Pair<Plugin, Version>> plugins = new Vector<>();
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        initPreLanguageManager();
        Object ucp = getUCP();
        Method method = getMethod(ucp);
        File plugins = getPluginDir();
        intros = new Vector<>();
        Vector<DepencyItem> addonItems = new Vector<>();
        handlePluginJar(plugins, method, ucp, intros, addonItems);
        downloadDepenciesJars(addonItems);
        injectDepencies(method, ucp);
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
    public static void handlePluginJar(File plugins, Method method, Object ucp, final Vector<String> intros, final Vector<DepencyItem> addonItems) throws IOException, ParserConfigurationException, SAXException, InvocationTargetException, IllegalAccessException {
        for (File f : plugins.listFiles()){
            method.invoke(ucp, f.toURI().toURL());
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
        depenciesLoader.checkAndDownload(addonItems.toArray(new DepencyItem[0]));
        depenciesLoader.frame.setVisible(false);
    }
    public static void injectDepencies(Method method, Object ucp) throws ParserConfigurationException, IOException, SAXException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        for (DepencyItem item : DepenciesXMLHandler.load()){
            method.invoke(ucp, new File(item.getLocal()).toURI().toURL());
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
}
