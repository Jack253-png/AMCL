package com.mcreater.amcl.api.minecraftSaves.mca;

import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    static final int CHUNK_HEADER_SIZE = 5;
    private static final byte[] emptySector = new byte[4096];

    private final File fileName;
    private RandomAccessFile file;
    public final int[] offsets;
    private final int[] chunkTimestamps;
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0;
    public RegionFile(File path) {
        offsets = new int[SECTOR_INTS];
        chunkTimestamps = new int[SECTOR_INTS];
        fileName = path;
        sizeDelta = 0;
        try {
            if (path.exists()) {
                lastModified = path.lastModified();
            }
            file = new RandomAccessFile(path, "rw");
            if (file.length() < SECTOR_BYTES) {
                for (int i = 0; i < SECTOR_INTS; ++i) {
                    file.writeInt(0);
                }
                for (int i = 0; i < SECTOR_INTS; ++i) {
                    file.writeInt(0);
                }
                sizeDelta += SECTOR_BYTES * 2;
            }
            if ((file.length() & 0xfff) != 0) {
                for (int i = 0; i < (file.length() & 0xfff); ++i) {
                    file.write((byte) 0);
                }
            }
            int nSectors = (int) file.length() / SECTOR_BYTES;
            sectorFree = new ArrayList<>(nSectors);

            for (int i = 0; i < nSectors; ++i) {
                sectorFree.add(true);
            }
            sectorFree.set(0, false);
            sectorFree.set(1, false);
            file.seek(0);
            for (int i = 0; i < SECTOR_INTS; ++i) {
                int offset = file.readInt();
                offsets[i] = offset;
                if (offset != 0 && (offset >> 8) + (offset & 0xFF) <= sectorFree.size()) {
                    for (int sectorNum = 0; sectorNum < (offset & 0xFF); ++sectorNum) {
                        sectorFree.set((offset >> 8) + sectorNum, false);
                    }
                }
            }
            for (int i = 0; i < SECTOR_INTS; ++i) {
                int lastModValue = file.readInt();
                chunkTimestamps[i] = lastModValue;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
        if (outOfBounds(x, z)) {
            return null;
        }

        try {
            int offset = getOffset(x, z);
            if (offset == 0) {
                debugln("READ", x, z, "miss");
                return null;
            }

            int sectorNumber = offset >> 8;
            int numSectors = offset & 0xFF;

            if (sectorNumber + numSectors > sectorFree.size()) {
                debugln("READ", x, z, "invalid sector");
                return null;
            }

            file.seek((long) sectorNumber * SECTOR_BYTES);
            int length = file.readInt();

            if (length > SECTOR_BYTES * numSectors) {
                debugln("READ", x, z, "invalid length: " + length + " > 4096 * " + numSectors);
                return null;
            }

            byte version = file.readByte();
            if (version == VERSION_GZIP) {
                byte[] data = new byte[length - 1];
                file.read(data);
                DataInputStream ret = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
                debug("READ", x, z, "found gzip format\n");
                return ret;
            } else if (version == VERSION_DEFLATE) {
                byte[] data = new byte[length - 1];
                file.read(data);
                DataInputStream ret = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
                debug("READ", x, z, "found deflate format\n");
                return ret;
            }

            debugln("READ", x, z, "unknown version " + version);
            return null;
        } catch (IOException e) {
            debugln("READ", x, z, "exception");
            return null;
        }
    }
    public Vector<Map<String, Integer>> getChunks(){
        Vector<Map<String, Integer>> result = new Vector<>();
        for (int x = 0;x < 32;x++){
            for (int z = 0;z < 32;z++){
                if (!outOfBounds(x, z) && getOffset(x, z) != 0){
                    Map<String, Integer> temp = new LinkedTreeMap<>();
                    temp.put("x", x);
                    temp.put("z", z);
                    result.add(temp);
                }
            }
        }
        return result;
    }

    /* is this an invalid chunk coordinate? */
    public boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    public void close() throws IOException {
        file.close();
    }
    private void debug(String in) {
        System.out.print(in);
    }

    private void debugln(String in) {
        debug(in + "\n");
    }

    private void debug(String mode, int x, int z, String in) {
        debug("REGION " + mode + " " + fileName.getName() + "[" + x + "," + z + "] = " + in);
    }

    private void debug(String mode, int x, int z, int count, String in) {
        debug("REGION " + mode + " " + fileName.getName() + "[" + x + "," + z + "] " + count + "B = " + in);
    }

    private void debugln(String mode, int x, int z, String in) {
        debug(mode, x, z, in + "\n");
    }
}
