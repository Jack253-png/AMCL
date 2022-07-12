package com.mcreater.amcl.api.minecraftSaves;

import org.jnbt.NBTHelper;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static org.jnbt.NBTHelper.*;

public class SaveDic {
    static final String old_path = "D:\\mods\\s\\.minecraft\\versions\\b1.0\\saves\\World1\\level.dat";
    static final String mid_path = "D:\\mods\\util\\.minecraft\\versions\\1.2.5\\saves\\新的世界\\level.dat";
    static final String path = "D:\\mods\\s\\.minecraft\\versions\\1.12.2\\saves\\New World\\level.dat";
    static final String new_path = "D:\\mods\\util\\.minecraft\\versions\\1.18.2\\saves\\新的世界\\level.dat";
    public static void main(String[] args) throws IOException {
        Vector<String> v = new Vector<>(List.of(new String[]{old_path, mid_path, path, new_path}));
        for (String p : v) {
            Tag tag = new NBTInputStream(new FileInputStream(p)).readTag();
//            System.out.println(NBTHelper.getNonTerimalMap(tag, "Data"));
            System.err.println(getSeed(tag));
            System.err.println(getSpawnPos(tag));
            System.err.println(getTime(tag));
            System.err.println(getLastPlayed(tag));
            System.err.println(getVersion(tag));
            System.err.println(getLevelName(tag));
            System.err.println(getGeneratorType(tag));
            System.err.println("-".repeat(50));
        }
    }
    public static long getSeed(Tag tag){
        try{
            return getPathLong(tag, "Data.RandomSeed");
        }
        catch (NullPointerException e){
            return getPathLong(tag, "Data.WorldGenSettings.seed");
        }
    }
    public static SpawnPos getSpawnPos(Tag tag){
        return new SpawnPos(getPathInt(tag, "Data.SpawnX"), getPathInt(tag, "Data.SpawnY"), getPathInt(tag, "Data.SpawnZ"));
    }
    public static long getTime(Tag tag){
        return getPathLong(tag, "Data.Time");
    }
    public static String getLastPlayed(Tag tag){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(getPathLong(tag, "Data.LastPlayed")));
    }
    public static String getVersion(Tag tag){
        try{
            return getPathString(tag, "Data.Version.Name");
        }
        catch (NullPointerException e){
            return null;
        }
    }
    public static String getLevelName(Tag tag){
        try{
            return getPathString(tag, "Data.LevelName");
        }
        catch (NullPointerException e){
            return null;
        }
    }
    public static String getGeneratorType(Tag tag){
        try{
            return getPathString(tag, "Data.generatorName");
        }
        catch (NullPointerException e){
            return null;
        }
    }
}
