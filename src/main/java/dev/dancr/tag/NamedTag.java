package dev.dancr.tag;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class NamedTag<T> extends Tag {

    private T payload;

    public NamedTag(DataInputStream stream) {
        super(stream);
    }

    public abstract int getTagID();

    public T getValue() {
        return payload;
    }

    protected void setPayload(T payload) {
        this.payload = payload;
    }
}
