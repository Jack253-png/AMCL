package com.mcreater.amcl.api.minecraftSaves;

import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.Tag;

import java.util.Collection;
import java.util.List;

public class Rotation {
    private final List<Object> content;
    public Rotation(Collection<FloatTag> raw){
        this.content = List.of(raw.toArray());
    }
    private double getFromList(int index){
        return ((FloatTag) content.get(index)).getValue();
    }
    public double getHoriznal(){
        return getFromList(0);
    }
    public double getVerital(){
        return getFromList(1);
    }
    public String toString(){
        return String.format("Rotation(h = %f, v = %f)", getHoriznal(), getVerital());
    }
}
