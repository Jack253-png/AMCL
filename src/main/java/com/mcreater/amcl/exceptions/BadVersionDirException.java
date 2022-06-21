package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadVersionDirException extends LaunchException {
    public BadVersionDirException(){}
    @Override
    public String toString() {
        return Application.languageManager.get("exceptions.BadVersionDirException.name");
    }
}
