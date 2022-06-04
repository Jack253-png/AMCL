package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadNativeDirException extends LaunchException{
    public BadNativeDirException(){}
    @Override
    public String toString() {
        return HelloApplication.languageManager.get("exceptions.BadNativeDirException.name");
    }
}
