package com.mcreater.amcl.api.reflect;

import java.lang.reflect.Field;
import java.net.MalformedURLException;

public class ReflectHelper {
    public static ReflectedJar getReflectedJar(String path) throws MalformedURLException {
        return new ReflectedJar(path);
    }
    public static Field[] getFields(Object instance){
        return instance.getClass().getDeclaredFields();
    }
    public static void setField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
