package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadNativeDirException extends LaunchException{
    public BadNativeDirException(){}
    @Override
    public String toString() {
        return Launcher.languageManager.get("exceptions.BadNativeDirException.name");
    }
}
