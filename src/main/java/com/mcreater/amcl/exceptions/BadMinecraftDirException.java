package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadMinecraftDirException extends LaunchException{
    public BadMinecraftDirException(){}
    @Override
    public String toString(){
        return Launcher.languageManager.get("exceptions.BadMinecraftDirException.name");
    }
}
