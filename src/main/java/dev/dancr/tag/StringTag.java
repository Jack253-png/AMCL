package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;

public class StringTag extends NamedTag<String> {

    public StringTag(DataInputStream stream) {
        super(stream);
    }

    @Override
    public int getTagID() {
        return 8;
    }
    public String toString(){
        return String.format("\"%s\"", getValue());
    }

    @Override
    public void read() {
        // value is the string
        short valueLength = ByteUtils.toShort(ByteUtils.safelyReadBytes(getInputStream(), Short.BYTES));
        setPayload(new String(ByteUtils.safelyReadBytes(getInputStream(), valueLength)));
    }
}
