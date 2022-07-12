package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;

public class FloatTag extends NumberTag<Float> {

    public FloatTag(DataInputStream stream) {
        super(stream, ByteUtils::toFloat);
    }

    @Override
    public int getTagID() {
        return 5;
    }

    @Override
    public short getPayloadSize() {
        return Float.BYTES;
    }
}
