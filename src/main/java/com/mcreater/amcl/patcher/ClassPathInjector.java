package com.mcreater.amcl.patcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathInjector {
    static Object ucp = null;
    static Method method;
    public static int version;
    public static boolean javafx_useable;
    static {
        try {
            ucp = getUCP();
            method = getMethod(ucp);
            method.setAccessible(true);
            String[] ful = System.getProperty("java.runtime.version").split("\\.");
            if (Integer.parseInt(ful[0]) == 1){
                version = Integer.parseInt(ful[1]);
            }
            else {
                version = Integer.parseInt(ful[0]);
            }
        }
        catch (Exception ignored){
            try {
                method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Class.forName("javafx.application.Application");
            javafx_useable = true;
        }
        catch (ClassNotFoundException e){
            javafx_useable = false;
        }
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
    public static void addJarUrl(URL url) throws InvocationTargetException, IllegalAccessException {
        if (version >= 9) {
            method.invoke(ucp, url);
        }
        else if (!url.toString().contains("openjfx")){
            method.invoke((URLClassLoader) ClassLoader.getSystemClassLoader(), url);
        }
    }
}
