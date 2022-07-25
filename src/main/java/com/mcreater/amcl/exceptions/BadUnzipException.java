package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadUnzipException extends LaunchException{
    public BadUnzipException(){}
    @Override
    public String toString() {
        return Launcher.languageManager.get("exceptions.BadUnzipException.name");
    }
}
