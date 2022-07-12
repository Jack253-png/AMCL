package dev.dancr;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class ByteUtils {

    public static byte[] safelyReadBytes(DataInputStream stream, int n) {
        try {
            return stream.readNBytes(n);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Byte[] toByteArray(byte[] bytes, int length) {
        Byte[] byteObjects = new Byte[length];
        Arrays.setAll(byteObjects, n -> bytes[n]);
        return byteObjects;
    }

    public static short toShort(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > Short.BYTES) throw new IllegalArgumentException("Byte array must be size 2");
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static int toInt(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > Integer.BYTES) throw new IllegalArgumentException("Byte array must be size 4");
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static Integer[] toIntArray(byte[] bytes, int arrayLength) {
        int[] ints = ByteBuffer.wrap(bytes).asIntBuffer().array();
        return Arrays.stream(ints).boxed().toArray(Integer[]::new);
    }

    public static float toFloat(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > Float.BYTES) throw new IllegalArgumentException("Byte array must be size 4");
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static long toLong(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > Long.BYTES) throw new IllegalArgumentException("Byte array must be size 8");
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static Long[] toLongArray(byte[] bytes, int arrayLength) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        long[] l = new long[longBuffer.capacity()];
        longBuffer.get(l);

        return Arrays.stream(l).boxed().toArray(Long[]::new);
    }

    public static double toDouble(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > Double.BYTES) throw new IllegalArgumentException("Byte array must be size 8");
        return ByteBuffer.wrap(bytes).getDouble();
    }
}
