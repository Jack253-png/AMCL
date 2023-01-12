package com.mcreater.amcl.model.download;

public class OriginalVersionModel {
    public String id;
    public String type;
    public String time;
    public String url;
    public OriginalVersionModel(String id, String type, String time, String url){
        this.id = id;
        this.type = type;
        this.time = time;
        this.url = url;
    }
    public String toString(){
        return this.type;
    }
}
