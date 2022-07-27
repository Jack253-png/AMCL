package com.mcreater.amcl.util.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class JavaHeapMemoryReader {
    public static long getUsedMem(){
        return getMaxMem() - Runtime.getRuntime().freeMemory();
    }
    public static long getMaxMem(){
        return Runtime.getRuntime().totalMemory();
    }
}
