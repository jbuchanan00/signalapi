package com.inkedout.Signal.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NectarClient {
    @Value("${nectar.url}")
    private String nectarUrl;
    public WebClientInstance nectarInstance;


    NectarClient(){
        init();
    }

    @PostConstruct
    void init(){
        this.nectarInstance = new WebClientInstance(nectarUrl);
    }
}
