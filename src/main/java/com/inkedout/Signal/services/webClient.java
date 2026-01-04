package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

public class webClient {
    private final RestClient restClient;

    @Value("${halo.url}")
    private String haloUrl;

    public webClient(RestClient.Builder restClientBuilder){
        this.restClient = restClientBuilder.baseUrl(haloUrl).build();

    }
}
