package com.mcreater.amcl.api.nbt;

import org.jnbt.mca.RegionFile;

import java.io.File;
import java.io.IOException;

public class NBTInputStream {
    public static void main(String[] args) throws IOException {
        RegionFile file = new RegionFile(new File("D:\\mods\\shaders\\.minecraft\\versions\\MTR Operation\\saves\\新的世界 (2)\\region\\r.0.0.mca"));
        org.jnbt.NBTInputStream stream1 = new org.jnbt.NBTInputStream(file.getChunkDataInputStream(0, 0));
        System.out.println(stream1.readTag());
    }
}
