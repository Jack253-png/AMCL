package com.mcreater.amcl.util.xml;

import com.mcreater.amcl.patcher.depenciesLoader;
import com.mcreater.amcl.util.J8Utils;

import java.net.MalformedURLException;
import java.util.List;

public class DepencyItem {
    String name;
    String maven;
    public DepencyItem(String name, String maven){
        this.name = name;
        this.maven = maven;
    }
    public String getLocal(){
        return "AMCL/depencies/" + depenciesLoader.convertName(this.name);
    }

    public String getURL() throws MalformedURLException {
        return this.maven + depenciesLoader.convertNameToUrl(this.name);
    }
    public String toString(){
        return this.name;
    }
}
