package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Function;

public abstract class NumberTag<T extends Number> extends NamedTag<T> implements IStaticPayload {

    private final Function<byte[], T> func;

    public NumberTag(DataInputStream stream, Function<byte[], T> func) {
        super(stream);
        this.func = func;
    }

    @Override
    public void read() {
        byte[] bytes;
        bytes = ByteUtils.safelyReadBytes(getInputStream(), getPayloadSize());
        setPayload(func.apply(bytes));
    }
}
