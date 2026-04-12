package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;

public class PolvoClient {
    @Value("${polvo.url}")
    private static String polvoUrl;

    static WebClientInstance polvoInstance;

    static void init(){
        polvoInstance = new WebClientInstance(polvoUrl);
    }

    public static WebClientInstance getInstance(){
        if(polvoInstance == null){
            init();
        }
        return polvoInstance;
    }
}
