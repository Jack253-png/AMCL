package com.mcreater.amcl.patcher;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import static com.mcreater.amcl.StableMain.getMethod;
import static com.mcreater.amcl.StableMain.getUCP;

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
    public static void addJarUrl(URL url) throws InvocationTargetException, IllegalAccessException {
        method.invoke(ucp, url);
    }
}
