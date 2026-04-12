package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;

public class HaloClient {
    @Value("${halo.url}")
    private static String haloUrl;

    static WebClientInstance haloInstance;

    static void init(){
        haloInstance = new WebClientInstance(haloUrl);
    }

    public static WebClientInstance getInstance(){
        if(haloInstance == null){
            init();
        }
        return haloInstance;
    }
}
