package com.mcreater.amcl;

import com.mcreater.amcl.javafx.depenciesLoader;
import com.mcreater.amcl.lang.PreLanguageManager;
import com.mcreater.amcl.util.LocateHelper;
import com.mcreater.amcl.util.xml.DepenciesXMLHandler;
import com.mcreater.amcl.util.xml.DepencyItem;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Vector;

public class StableMain {
    public static PreLanguageManager manager;
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ParserConfigurationException, IOException, InterruptedException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        manager = new PreLanguageManager(PreLanguageManager.valueOf(LocateHelper.get()));
        manager.initlaze();
        depenciesLoader.checkAndDownload();
        depenciesLoader.frame.setVisible(false);
        Vector<URL> jars = new Vector<>();
        Field field = ClassLoader.getSystemClassLoader().getClass().getDeclaredField("ucp");
        field.setAccessible(true);
        Object ucp = field.get(ClassLoader.getSystemClassLoader());
        Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        for (DepencyItem item : DepenciesXMLHandler.load()){
            URL u = new File(item.getLocal()).toURI().toURL();
            jars.add(u);
            method.invoke(ucp, u);
        }
        Main.main(args);
    }
}
