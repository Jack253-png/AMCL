package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListTag extends NamedTag<List<Tag>> {

    private Class<? extends Tag> classType;

    public ListTag(DataInputStream stream) {
        super(stream);
    }

    @Override
    public int getTagID() {
        return 9;
    }

    @Override
    public void read() {
        try {
            this.classType = CompoundTag.NBT_TAGS.get(Byte.toUnsignedInt(getInputStream().readByte()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int listSize = ByteUtils.toInt(ByteUtils.safelyReadBytes(getInputStream(), Integer.BYTES));
        List<Tag> tags = new ArrayList<>();

        if (listSize != 0) {
            for (int i = 0; i < listSize; i++) {
                try {
                    tags.add(classType.getDeclaredConstructor(DataInputStream.class).newInstance(getInputStream()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            tags.forEach(Tag::read);
        }
        setPayload(tags);
    }

    @Override
    public List<Tag> getValue() {
        // Casts the payload list of base classes to the class type provided
        return super.getValue().stream().map(classType::cast).collect(Collectors.toList());
    }

    public <T extends Tag> T get(int index, Class<T> expectedType) {
        if (expectedType != classType) return null;
        return expectedType.cast(getValue().get(index));
    }

    public int size() {
        return getValue().size();
    }
}
