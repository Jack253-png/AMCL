package org.jnbt;

import org.jnbt.tags.Tag;

import java.util.Collection;
import java.util.Map;

public final class NBTHelper {
    public static Tag get(Tag tag, String s){
        return (Tag) ((Map<?, ?>) tag.getValue()).get(s);
    }
    public static Tag getPath(Tag tag, String path){
        Tag t = tag;
        for (String s : path.split("\\.")){
            t = get(t, s);
        }
        return t;
    }
    public static Tag getNonTerimalMap(Tag tag, String path){
        return getPath(tag, path);
    }
    public static long getPathLong(Tag tag, String path){
        return (long) getPath(tag, path).getValue();
    }
    public static int getPathInt(Tag tag, String path){
        return (int) getPath(tag, path).getValue();
    }
    public static short getPathShort(Tag tag, String path){
        return (short) getPath(tag, path).getValue();
    }
    public static String getPathString(Tag tag, String path){
        return (String) getPath(tag, path).getValue();
    }
    public static Collection<?> getPathList(Tag tag, String path){
        return (Collection<?>) getPath(tag, path).getValue();
    }

}
