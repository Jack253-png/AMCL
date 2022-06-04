package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.HelloApplication;

public class BadMainFilesException extends LaunchException{
    public BadMainFilesException(){}
    @Override
    public String toString() {
        return HelloApplication.languageManager.get("exceptions.BadMainFiles.name");
    }
}
