package com.mcreater.amcl.api.minecraftSaves;

import org.jnbt.tags.DoubleTag;

import java.util.Collection;
import java.util.List;

public class Motion {
    private final List<Object> content;
    public Motion(Collection<DoubleTag> raw){
        this.content = List.of(raw.toArray());
    }
    private double getFromList(int index){
        return ((DoubleTag) content.get(index)).getValue();
    }
    public double getAX(){
        return getFromList(0);
    }
    public double getAY(){
        return getFromList(1);
    }
    public double getAZ(){
        return getFromList(2);
    }
    public String toString(){
        return String.format("Motion(ax = %f, ay = %f, az = %f)", getAX(), getAY(), getAZ());
    }
}
