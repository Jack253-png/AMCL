package com.mcreater.amcl.util.system;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MemoryDataReader {
    static Unsafe unsafe;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printMemsToOutPut(){

    }
}
