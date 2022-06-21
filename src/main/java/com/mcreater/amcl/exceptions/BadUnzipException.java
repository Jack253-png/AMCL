package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadUnzipException extends LaunchException{
    public BadUnzipException(){}
    @Override
    public String toString() {
        return Application.languageManager.get("exceptions.BadUnzipException.name");
    }
}
