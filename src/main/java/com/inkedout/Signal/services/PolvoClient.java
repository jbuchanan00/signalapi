package com.inkedout.Signal.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PolvoClient {
    @Value("${polvo.url}")
    private String polvoUrl;
    public WebClientInstance polvoInstance;


    PolvoClient(){
        init();
    }

    @PostConstruct
    void init(){
        this.polvoInstance = new WebClientInstance(polvoUrl);
    }

}
