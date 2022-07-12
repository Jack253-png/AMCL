package com.mcreater.amcl.api.minecraftSaves;

public class SpawnPos {
    public int x;
    public int y;
    public int z;
    public SpawnPos(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    public String toString(){
        return String.format("SpawnPos(x = %d, y = %d, z = %d)", getX(), getY(), getZ());
    }
}
