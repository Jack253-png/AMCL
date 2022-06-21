package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadNativeDirException extends LaunchException{
    public BadNativeDirException(){}
    @Override
    public String toString() {
        return Application.languageManager.get("exceptions.BadNativeDirException.name");
    }
}
