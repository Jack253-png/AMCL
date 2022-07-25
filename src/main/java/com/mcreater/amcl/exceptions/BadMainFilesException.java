package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadMainFilesException extends LaunchException{
    public BadMainFilesException(){}
    @Override
    public String toString() {
        return Launcher.languageManager.get("exceptions.BadMainFiles.name");
    }
}
