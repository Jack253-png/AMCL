package com.mcreater.amclAPI;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Version {
    String version() default "ALL-VERSION";
}
