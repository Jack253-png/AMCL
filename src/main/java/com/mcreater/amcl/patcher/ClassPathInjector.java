package com.mcreater.amcl.patcher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class ClassPathInjector {
    static Object ucp = null;
    static Method method = null;
    static {
        try {
            ucp = getUCP();
            method = getMethod(ucp);
        }
        catch (Exception e){

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
        method.invoke(ucp, url);
    }
}
