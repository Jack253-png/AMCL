package dev.dancr;

import dev.dancr.tag.ByteTag;
import dev.dancr.tag.CompoundTag;
import dev.dancr.tag.ListTag;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Main {

    private static final byte[] EMPTY_COMPOUND_TAG = {10, 0, 0};

    public static void main(String[] args) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        RegionFile file = new RegionFile(new File("resources/r.0.0.mca"));

        var s = file.getChunkDataInputStream(0, 0);
        System.out.println("\n");
        if (isInvalidNBT(s)) throw new InvalidObjectException("Chunk in MCA file must always begin with an empty compound tag");

        CompoundTag tag = new CompoundTag(s);
        tag.read();

        byte y = tag
                .get("sections", ListTag.class)
                .get(0, CompoundTag.class)
                .get("Y", ByteTag.class)
                .getValue();

        System.out.println(y);

    }

    /**
     * @return true if the input stream first 3 bytes isn't an empty compound tag
     */
    private static boolean isInvalidNBT(DataInputStream stream) throws IOException {
        return !(Arrays.equals(stream.readNBytes(EMPTY_COMPOUND_TAG.length), EMPTY_COMPOUND_TAG));
    }
}
