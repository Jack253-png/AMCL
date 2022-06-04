package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadMinecraftDirException extends LaunchException{
    public BadMinecraftDirException(){}
    @Override
    public String toString(){
        return HelloApplication.languageManager.get("exceptions.BadMinecraftDirException.name");
    }
}
