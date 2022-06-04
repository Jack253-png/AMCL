package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadVersionDirException extends LaunchException {
    public BadVersionDirException(){}
    @Override
    public String toString() {
        return HelloApplication.languageManager.get("exceptions.BadVersionDirException.name");
    }
}
