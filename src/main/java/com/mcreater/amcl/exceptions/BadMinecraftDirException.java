package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadMinecraftDirException extends LaunchException{
    public BadMinecraftDirException(){}
    @Override
    public String toString(){
        return Application.languageManager.get("exceptions.BadMinecraftDirException.name");
    }
}
