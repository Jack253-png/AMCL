package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadUnzipException extends LaunchException{
    public BadUnzipException(){}
    @Override
    public String toString() {
        return HelloApplication.languageManager.get("exceptions.BadUnzipException.name");
    }
}
