package com.mcreater.amcl.exceptions;

import com.mcreater.amcl.Launcher;

public class BadUserException extends LaunchException{
    public BadUserException(){}
    public String toString() {
        return Launcher.languageManager.get("exceptions.baduserexception.name");
    }
}
