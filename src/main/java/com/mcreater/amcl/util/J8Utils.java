package com.mcreater.amcl.util;

import com.mcreater.amcl.util.system.MemoryReader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class J8Utils {
    public static String rawToString(Object o){
        return String.format("%s@%s", o.getClass(), Integer.toHexString(o.hashCode()));
    }
    public static <K, V> Map<K, V> createMap(Class<K> k, Class<V> v, Object... values){
        Map<K, V> map = new HashMap<>();
        if (values.length % 2 != 0){
            throw new StackOverflowError();
        }
        for (int i = 0;i < values.length;i += 2){
            map.put((K) values[i], (V) values[i+1]);
        }
        return map;
    }
    public static Map<Object, Object> createMap(Object... values){
        return createMap(Object.class, Object.class, values);
    }
    @SafeVarargs
    public static <E> List<E> createList(E... values){
        List<E> list = new Vector<>();
        Collections.addAll(list, values);
        return list;
    }
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
    }
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
    public static long getProcessPid(Process process) {
        try {
            Method m = Process.class.getDeclaredMethod("pid");
            return (long) m.invoke(process);
        }
        catch (Exception e){
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return f.getLong(process);
            }
            catch (Exception e2){
                return -1;
            }
        }
    }
    public static String repeat(String s, int r){
        StringBuilder b = new StringBuilder();
        for (int i = 0;i < r;i++){
            b.append(s);
        }
        return b.toString();
    }
    public static long getMcMaxMemory(){
        long value = MemoryReader.getTotalMemory() / 1024 / 1024;
        if (value < 1024) {
            return 1024;
        }
        else {
            return value;
        }
    }
}
