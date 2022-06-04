package com.mcreater.amcl.exceptions;

import java.io.IOException;

public class BadReturnCodeException extends IOException {
    public Integer returnCode = null;
    public BadReturnCodeException(){}
    public BadReturnCodeException(int returnCode){
        this.returnCode = returnCode;
    }
    public String toString(){
        return String.format("Bad Return Code : %d", this.returnCode);
    }
}
