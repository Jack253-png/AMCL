package com.mcreater.amcl.util;

import com.teamdev.jxbrowser.chromium.Cookie;

public class SerialableCookie implements Serializable {
    public long serialVersionUID = 3175017776834739074L;
    public final String url;
    public final String name;
    public final String value;
    public final String domain;
    public final String path;
    public final long expirationTimeInMicroseconds;
    public final boolean secure;
    public final boolean httpOnly;
    public SerialableCookie(Cookie cookie) {
        url = cookie.getDomain();
        name = cookie.getName();
        value = cookie.getValue();
        domain = cookie.getDomain();
        path = cookie.getPath();
        expirationTimeInMicroseconds = cookie.getExpirationTime();
        secure = cookie.isSecure();
        httpOnly = cookie.isHTTPOnly();
    }
}
