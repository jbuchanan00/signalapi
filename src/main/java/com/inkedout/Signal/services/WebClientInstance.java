package com.inkedout.Signal.services;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientInstance {
    private final WebClient client;

    public WebClientInstance(String baseAddress){
        this.client = WebClient.create(baseAddress);
    }

    public WebClient.ResponseSpec getData(String uri) {

        return client.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Client Error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server Error: " + response.statusCode())));
    }

    public WebClient.ResponseSpec postData(String uri, Object body){
        return client.post().uri(uri).body(BodyInserters.fromValue(body)).retrieve().onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("Client Error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server Error: " + response.statusCode())));
    }
}
