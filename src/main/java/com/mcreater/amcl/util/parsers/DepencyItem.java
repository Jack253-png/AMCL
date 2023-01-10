package com.mcreater.amcl.util.parsers;

import com.mcreater.amcl.patcher.DepenciesLoader;

import java.net.MalformedURLException;

import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class DepencyItem {
    public String name;
    String maven;
    public DepencyItem(String name, String maven){
        this.name = name;
        this.maven = maven;
    }
    public String getLocal(){
        return String.format(buildPath("AMCL", "depencies", "%s"), DepenciesLoader.convertName(this.name));
    }

    public String getURL() throws MalformedURLException {
        return this.maven + DepenciesLoader.convertNameToUrl(this.name);
    }
    public String toString(){
        return this.name;
    }
}
