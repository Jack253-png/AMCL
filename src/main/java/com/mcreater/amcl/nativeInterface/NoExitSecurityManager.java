package com.mcreater.amcl.nativeInterface;

import java.security.Permission;

public class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
    }

    @Override
    public void checkExit(int status) {
        super.checkExit(status);
        throw new SecurityException();
    }
}
