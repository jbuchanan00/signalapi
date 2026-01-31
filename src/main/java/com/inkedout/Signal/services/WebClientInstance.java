package com.inkedout.Signal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientInstance {
    private static final Logger log = LoggerFactory.getLogger(WebClientInstance.class);
    private final WebClient client;

    public WebClientInstance(String baseAddress){
        this.client = WebClient.create(baseAddress);
        log.info("Client base is: {}", baseAddress);
    }

    public WebClient.ResponseSpec getData(String uri) {

        return client.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Client Error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server Error: " + response.statusCode())));
    }

    public WebClient.ResponseSpec postData(String uri, RequestBody body){
        return client.post().bodyValue(body).retrieve().onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Client Error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server Error: " + response.statusCode())));
    }
}
