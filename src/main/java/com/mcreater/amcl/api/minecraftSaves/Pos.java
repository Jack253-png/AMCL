package com.mcreater.amcl.api.minecraftSaves;

import org.jnbt.DoubleTag;
import org.jnbt.Tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Pos {
    private final List<Object> content;
    public Pos(Collection<DoubleTag> raw){
        this.content = List.of(raw.toArray());
    }
    private double getFromList(int index){
        return ((DoubleTag) content.get(index)).getValue();
    }
    public double getX(){
        return getFromList(0);
    }
    public double getY(){
        return getFromList(1);
    }
    public double getZ(){
        return getFromList(2);
    }
    public String toString(){
        return String.format("Pos(x = %f, y = %f, z = %f)", getX(), getY(), getZ());
    }
}
