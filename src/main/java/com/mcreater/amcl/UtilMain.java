package com.mcreater.amcl;

import com.mcreater.amcl.util.net.HttpConnectionUtil;

public class UtilMain {
    public static void main(String[] args){
        System.out.println(HttpConnectionUtil.doGet("https://bmclapi2.bangbang93.com/fabric-meta/v2/versions/loader/1.18.1"));
    }
}