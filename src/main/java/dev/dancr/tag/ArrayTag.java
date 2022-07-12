package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.util.function.BiFunction;

public abstract class ArrayTag<T> extends NamedTag<T[]> implements IStaticPayload {

    // (byte array, length -> T[])
    private final BiFunction<byte[], Short, T[]> func;

    public ArrayTag(DataInputStream stream, BiFunction<byte[], Short, T[]> func) {
        super(stream);
        this.func = func;
    }

    @Override
    public void read() {
        int arrayLength = ByteUtils.toInt(ByteUtils.safelyReadBytes(getInputStream(), Integer.BYTES));

        setPayload(func.apply(ByteUtils.safelyReadBytes(getInputStream(), arrayLength * getPayloadSize()), getPayloadSize()));
    }

}
