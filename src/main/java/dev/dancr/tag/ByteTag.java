package dev.dancr.tag;

import java.io.DataInputStream;
import java.io.IOException;

public class ByteTag extends NumberTag<Byte> {

    public ByteTag(DataInputStream stream) {
        super(stream, (bytes) -> bytes[0]);
    }

    @Override
    public int getTagID() {
        return 1;
    }

    @Override
    public short getPayloadSize() {
        return Byte.BYTES;
    }
}
