package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;

public class LongTag extends NumberTag<Long> {

    public LongTag(DataInputStream stream) throws IOException {
        super(stream, ByteUtils::toLong);
    }

    @Override
    public int getTagID() {
        return 4;
    }
    public String toString(){
        return getValue().toString();
    }

    @Override
    public short getPayloadSize() {
        return Long.BYTES;
    }
}
