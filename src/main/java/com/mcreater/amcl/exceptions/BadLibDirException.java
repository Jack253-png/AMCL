package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadLibDirException extends LaunchException{
    public BadLibDirException(){}
    @Override
    public String toString() {
        return Application.languageManager.get("exceptions.BadLibDirException.name");
    }
}
