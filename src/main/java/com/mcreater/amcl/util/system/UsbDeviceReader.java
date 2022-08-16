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
    public static Vector<UsbDevice> getDevices(){
        return new Vector<>(J8Utils.createList(hal.getUsbDevices(true)));
    }
}
