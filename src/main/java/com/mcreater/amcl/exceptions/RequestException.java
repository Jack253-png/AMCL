package com.mcreater.amcl.exceptions;

import java.io.IOException;

public class RequestException extends IOException {
    public RequestException() {
        super();
    }
    public RequestException(String message) {
        super(message);
    }
    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
