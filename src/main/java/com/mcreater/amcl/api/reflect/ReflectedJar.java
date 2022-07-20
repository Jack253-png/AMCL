package com.mcreater.amcl.api.reflect;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ReflectedJar {
    URLClassLoader loader;
    Vector<Object> instances = new Vector<>();
    public ReflectedJar(String... path) throws MalformedURLException {
        URL[] urls = new URL[List.of(path).size()];
        Vector<String> vs = new Vector<>(List.of(path));
        for(int i = 0;i < vs.size();i++) {
            urls[i] = new File(vs.get(i)).toURL();
        }
        loader = new URLClassLoader(urls);
    }
    public Class<?> getJarClass(String className) throws ClassNotFoundException {
        return loader.loadClass(className);
    }
    public int createNewInstance(Class<?> cl) throws InstantiationException, IllegalAccessException {
        instances.add(cl.newInstance());
        return instances.size() - 1;
    }
    public Object getFieldContent(int id, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field f = instances.get(id).getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(instances.get(id));
    }
    public Object invokeStaticMethod(Class<?> cl, String method_name, Object[] args, Class<?>... types) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = cl.getDeclaredMethod(method_name, types);
        m.setAccessible(true);
        return m.invoke(null, args);
    }
    public Object invokeNoArgsMethod(int id, String method_name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object o = instances.get(id);
        Method m = o.getClass().getDeclaredMethod(method_name);
        m.setAccessible(true);
        return m.invoke(o);
    }
    public Object invokeMethod(int id, String method_name, Object[] args, Class<?>... types) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object instance = instances.get(id);
        Method m = instance.getClass().getDeclaredMethod(method_name, types);
        m.setAccessible(true);
        return m.invoke(instance, args);
    }
}
