package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;

public class NectarClient {
    @Value("${nectar.url")
    private static String nectarUrl;

    static WebClientInstance nectarInstance;

    static void init(){
        nectarInstance = new WebClientInstance(nectarUrl);
    }

    public static WebClientInstance getInstance(){
        if(nectarInstance == null){
            init();
        }
        return nectarInstance;
    }
}
