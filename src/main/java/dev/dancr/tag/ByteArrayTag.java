package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;

public class ByteArrayTag extends ArrayTag<Byte> {

    public ByteArrayTag(DataInputStream stream) {
        super(stream, ByteUtils::toByteArray);
    }

    @Override
    public short getPayloadSize() {
        return Byte.BYTES;
    }

    @Override
    public int getTagID() {
        return 7;
    }
}
