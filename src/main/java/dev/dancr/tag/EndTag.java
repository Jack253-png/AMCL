package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;

public class EndTag extends Tag {

    public EndTag(DataInputStream inputStream) {
        super(inputStream);
    }

    @Override
    public void read() {
        // This'll always be 0. It has no name.
        //ByteUtils.safelyReadBytes(getInputStream(), Byte.BYTES);
    }
}
