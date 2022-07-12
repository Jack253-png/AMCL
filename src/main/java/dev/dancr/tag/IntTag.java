package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;

public class IntTag extends NumberTag<Integer> {

    public IntTag(DataInputStream stream) {
        super(stream, ByteUtils::toInt);
    }

    @Override
    public int getTagID() {
        return 3;
    }

    @Override
    public short getPayloadSize() {
        return Integer.BYTES;
    }
}
