package org.jnbt;

import org.jnbt.tags.CompoundTag;
import org.jnbt.tags.ListTag;
import org.jnbt.tags.Tag;

public class NBTMapUtils {
    public static Tag getTagWithPath(Tag tag, String path) {
        try {
            Tag temp = tag.toTag(CompoundTag.class);
            if (path == null){
                return temp;
            }
            for (String t2 : path.split("/")){
                if (t2.contains("[") && t2.contains("]")){
                    String name = t2.substring(0, t2.indexOf("["));
                    int index = Integer.parseInt(t2.substring(t2.indexOf("[") + 1, t2.indexOf("]")));
                    temp = temp
                            .toTag(CompoundTag.class)
                            .getValue()
                            .get(name)
                            .toTag(ListTag.class)
                            .getValue()
                            .get(index);
                }
                else {
                    temp = temp
                            .toTag(CompoundTag.class)
                            .getValue()
                            .get(t2);
                }
            }
            return temp;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
