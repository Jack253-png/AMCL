package net.querz.nbt;

import com.mcreater.amcl.util.J8Utils;
import net.querz.mca.Chunk;
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

import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

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

        if (root.getValue() instanceof int[]) {
            return J8Utils.createListFromArray((int[]) root.getValue());
        }
        if (root.getValue() instanceof long[]) {
            return J8Utils.createListFromArray((long[]) root.getValue());
        }
        if (root.getValue() instanceof byte[]) {
            return J8Utils.createListFromArray((byte[]) root.getValue());
        }

        return root.getValue();
    }
    public static Object toJavaNativeDataType(NamedTag root) {
        return J8Utils.createMap(root.getName(), toJavaNativeDataType(root.getTag()));
    }
    public static Object toJavaNativeDataType(Chunk chunk) {
        return toJavaNativeDataType(chunk.data);
    }

    public static String toJsonString(Object v) {
        return GSON_PARSER.toJson(v);
    }
}
