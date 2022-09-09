package com.mcreater.amcl.util.system;

import com.mcreater.amcl.util.J8Utils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.UsbDevice;

import java.util.List;
import java.util.Vector;

public class UsbDeviceReader {
    static SystemInfo si = new SystemInfo();
    static HardwareAbstractionLayer hal = si.getHardware();
    public static List<UsbDevice> getDevices(){
        return hal.getUsbDevices(true);
    }
}
