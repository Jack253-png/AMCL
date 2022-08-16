package org.jnbt.tags;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class LongArrayTag extends Tag{
    private final long[] value;

    /**
     * Creates the tag.
     *
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    public LongArrayTag(final String name, final long[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }
    public Vector<Long> toNativeType(){
        Vector<Long> ls = new Vector<>();
        for (Long l : value){
            ls.add(l);
        }
        return ls;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + Arrays.hashCode(value);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (!super.equals(obj)) { return false; }
        if (!(obj instanceof LongArrayTag)) { return false; }
        final LongArrayTag other = (LongArrayTag) obj;
        if (!Arrays.equals(value, other.getValue())) { return false; }
        return true;
    }
}
