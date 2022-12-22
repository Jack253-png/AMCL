package com.mcreater.amcl.util.parsers;

import com.mcreater.amcl.patcher.DepenciesLoader;

import java.net.MalformedURLException;

public class DepencyItem {
    public String name;
    String maven;
    public DepencyItem(String name, String maven){
        this.name = name;
        this.maven = maven;
    }
    public String getLocal(){
        return "AMCL/depencies/" + DepenciesLoader.convertName(this.name);
    }

    public String getURL() throws MalformedURLException {
        return this.maven + DepenciesLoader.convertNameToUrl(this.name);
    }
    public String toString(){
        return this.name;
    }
}
