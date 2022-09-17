package net.querz.nbt;

import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.EndTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

public final class NBTUtils {
    public static Object toJavaNativeDataType(Tag<?> root) {
        if (root instanceof EndTag) {
            return null;
        }

        if (root instanceof CompoundTag) {
            CompoundTag temp = (CompoundTag) root;
            Map<String, Object> nat = new HashMap<>();
            temp.forEach((s, tag) -> nat.put(s, toJavaNativeDataType(tag)));
            return nat;
        }
        if (root instanceof ListTag) {
            ListTag<?> temp = (ListTag<?>) root;
            List<Object> nat = new Vector<>();
            temp.getValue().forEach((Consumer<Object>) o -> {
                if (o instanceof Tag) {
                    nat.add(toJavaNativeDataType((Tag<?>) o));
                }
                else {
                    nat.add(o);
                }
            });
            return nat;
        }

        return root.getValue();
    }
}
