package dev.dancr.tag;

import dev.dancr.ByteUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class CompoundTag extends Tag {

    private static final int TAG_END = 0;
    public static final Map<Integer, Class<? extends Tag>> NBT_TAGS = Map.ofEntries(
            entry(0, EndTag.class),
            entry(1, ByteTag.class),
            entry(2, ShortTag.class),
            entry(3, IntTag.class),
            entry(4, LongTag.class),
            entry(5, FloatTag.class),
            entry(6, DoubleTag.class),
            entry(7, ByteArrayTag.class),
            entry(8, StringTag.class),
            entry(9, ListTag.class),
            entry(10, CompoundTag.class),
            entry(11, IntArrayTag.class),
            entry(12, LongArrayTag.class)
    );

    public String toString(){
        StringBuilder sb = new StringBuilder("{\r\n");
        int index = 0;
        for (Map.Entry<String, Tag> entry : tags.entrySet()){
            index++;
            sb.append(String.format("   \"%s\" : %s%s", entry.getKey(), entry.getValue().toString().replaceAll("\r\n", "\r\n   "), tags.entrySet().size() == index ? "" : ", \r\n"));
        }
        sb.append("    \r\n}");
        return sb.toString();
    }

    private final HashMap<String, Tag> tags = new HashMap<>();

    public CompoundTag(DataInputStream inputStream) {
        super(inputStream);
    }

    public void read() {
        try {
            while (getInputStream().available() > 0) {
                byte tagType = getInputStream().readByte();

                if (tagType == TAG_END) return;

                Class<? extends Tag> tagTypeClass = NBT_TAGS.get(Byte.toUnsignedInt(tagType));

                short nameLength = ByteUtils.toShort(ByteUtils.safelyReadBytes(getInputStream(), Short.BYTES));
                String name = new String(ByteUtils.safelyReadBytes(getInputStream(), nameLength));
                Tag obj = tagTypeClass.getDeclaredConstructor(DataInputStream.class).newInstance(getInputStream());
                obj.read();

                tags.put(name, obj);

            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends Tag> T get(String name, Class<T> expectedType) {
        Tag tag = tags.get(name);
        Objects.requireNonNull(tag);
        if (!expectedType.isInstance(tag)) {
            throw new ClassCastException("Tag with name " + name + " is not of type " + expectedType.getSimpleName());
        }
        return expectedType.cast(tag);
    }

    public int size() {
        return tags.size();
    }
}
