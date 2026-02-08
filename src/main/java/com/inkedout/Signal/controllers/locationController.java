package com.inkedout.Signal.controllers;


import com.inkedout.Signal.services.WebClientInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/location")
public class locationController {

    private static final Logger log = LoggerFactory.getLogger(locationController.class);
    @Value("${halo.url}")
    private String haloUrl;


    @CrossOrigin(origins= "*")
    @GetMapping("/autofill")
    @ResponseBody
    public Mono<String> getLocationAutofill(@RequestParam(name = "text") String subtext){
        WebClientInstance webClient = new WebClientInstance(haloUrl);

        return webClient.getData("/autofill?text="+subtext).bodyToMono(String.class);
    }

    @GetMapping("/helloworld")
    @ResponseBody
    public String getHelloWorld(){
        return "Hello World!";
    }
}
