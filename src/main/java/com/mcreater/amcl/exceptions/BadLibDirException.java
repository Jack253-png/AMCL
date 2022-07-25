package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadLibDirException extends LaunchException{
    public BadLibDirException(){}
    @Override
    public String toString() {
        return Launcher.languageManager.get("exceptions.BadLibDirException.name");
    }
}
