package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Application;

public class BadMainFilesException extends LaunchException{
    public BadMainFilesException(){}
    @Override
    public String toString() {
        return Application.languageManager.get("exceptions.BadMainFiles.name");
    }
}
