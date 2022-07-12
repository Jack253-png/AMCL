package dev.dancr.tag;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Tag {

    private final DataInputStream inputStream;

    public Tag(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public abstract void read();

    public DataInputStream getInputStream() {
        return inputStream;
    }
}
