package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;

public class DoubleTag extends NumberTag<Double> {

    public DoubleTag(DataInputStream stream) {
        super(stream, ByteUtils::toDouble);
    }

    @Override
    public int getTagID() {
        return 6;
    }

    @Override
    public short getPayloadSize() {
        return Double.BYTES;
    }
}
