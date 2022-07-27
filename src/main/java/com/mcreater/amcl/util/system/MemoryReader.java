package com.mcreater.amcl.util.system;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

public class MemoryReader {
    static SystemInfo si = new SystemInfo();
    static HardwareAbstractionLayer hal = si.getHardware();
    static GlobalMemory mem = hal.getMemory();
    public static String convertMemToString(long value){
        double d = (double) value;
        if (d < 1024){
            return String.format("%f B", d);
        }
        else if (d < 1024 * 1024){
            return String.format("%f KB", d / 1024);
        }
        else if (d < 1024 * 1024 * 1024){
            return String.format("%f MB", d / 1024 / 1024);
        }
        else {
            return String.format("%f GB", d / 1024 / 1024 / 1024);
        }
    }
    public static long getUsedMemory(){
        return mem.getTotal() - mem.getAvailable();
    }
    public static long getFreeMemory(){
        return mem.getAvailable();
    }
    public static long getTotalMemory(){
        return mem.getTotal();
    }
}
