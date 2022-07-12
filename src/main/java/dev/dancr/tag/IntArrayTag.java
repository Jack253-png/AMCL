package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;

public class IntArrayTag extends ArrayTag<Integer> {

    public IntArrayTag(DataInputStream stream) {
        super(stream, ByteUtils::toIntArray);
    }

    @Override
    public short getPayloadSize() {
        return Integer.BYTES;
    }

    @Override
    public int getTagID() {
        return 11;
    }
}
