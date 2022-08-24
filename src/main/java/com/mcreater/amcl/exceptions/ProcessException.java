package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class ProcessException extends LaunchException {
    public ProcessException(){}
    public String toString() {
        return Launcher.languageManager.get("exceptions.processexception.name");
    }
}
