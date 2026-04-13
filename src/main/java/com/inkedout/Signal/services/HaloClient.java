package com.inkedout.Signal.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HaloClient {
    @Value("${halo.url}")
    private String haloUrl;
    public WebClientInstance haloInstance;


    HaloClient(){
        init();
    }

    @PostConstruct
    void init(){
        this.haloInstance = new WebClientInstance(haloUrl);
    }
}
