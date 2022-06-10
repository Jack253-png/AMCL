package com.mcreater.amcl.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import static java.util.jar.Attributes.Name;

public class GetJarMainClass {
    public static String get(String jarFile) throws IOException {
        File f = new File(jarFile);
        JarFile jarFile1 = new JarFile(f);
        Attributes attrs = jarFile1.getManifest().getMainAttributes();
        for (Iterator<Object> it = attrs.keySet().iterator(); it.hasNext();){
            Name attrName = (Name) it.next();
            if (Objects.equals(attrName.toString(), "Main-Class")){
                return attrs.getValue(attrName);
            }
        }
        return null;
    }
}
