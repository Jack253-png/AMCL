package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends ArrayTag<Long> {

    public LongArrayTag(DataInputStream stream) throws IOException {
        super(stream, ByteUtils::toLongArray);
    }

    @Override
    public short getPayloadSize() {
        return Long.BYTES;
    }
    public String toString(){
        return Arrays.toString(getValue());
    }

    @Override
    public int getTagID() {
        return 12;
    }
}
