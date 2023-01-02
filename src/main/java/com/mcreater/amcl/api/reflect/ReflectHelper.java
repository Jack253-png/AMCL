package com.mcreater.amcl.api.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Consumer;

public class ReflectHelper {
    public static ReflectedJar getReflectedJar(String... path) throws Exception {
        return new ReflectedJar(path);
    }
    public static ReflectedJar getReflectedJar(URL... path) {
        return new ReflectedJar(path);
    }
    public static Field[] getFields(Object instance){
        return instance.getClass().getDeclaredFields();
    }
    public static void setField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
    public static <V> V invokeMethod(Object instance, String methodName, Class<V> returnType, Object... args) throws Exception {
        Vector<Class<?>> classTypes = new Vector<>();
        Arrays.stream(args).forEach(o -> classTypes.add(o.getClass()));

        Method m = instance.getClass().getDeclaredMethod(methodName, classTypes.toArray(new Class[0]));
        m.setAccessible(true);
        return (V) m.invoke(instance, args);
    }
    public static void invokeNoReturnMethod(Object instance, String methodName, Object... args) throws Exception {
        Vector<Class<?>> classTypes = new Vector<>();
        Arrays.stream(args).forEach(o -> classTypes.add(o.getClass()));

        Method m = instance.getClass().getDeclaredMethod(methodName, classTypes.toArray(new Class[0]));
        m.setAccessible(true);
        m.invoke(instance, args);
    }
}
