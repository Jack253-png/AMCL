package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadVersionDirException extends LaunchException {
    public BadVersionDirException(){}
    @Override
    public String toString() {
        return Launcher.languageManager.get("exceptions.BadVersionDirException.name");
    }
}
