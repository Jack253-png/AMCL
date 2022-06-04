package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadLibDirException extends LaunchException{
    public BadLibDirException(){}
    @Override
    public String toString() {
        return HelloApplication.languageManager.get("exceptions.BadLibDirException.name");
    }
}
