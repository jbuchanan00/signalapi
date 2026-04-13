package com.inkedout.Signal.controllers;


import com.inkedout.Signal.services.HaloClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/location")
public class LocationController {
    LocationController(HaloClient haloClient) {
        haloClientInstance = haloClient.haloInstance;
    }

    private final WebClientInstance haloClientInstance;

    @CrossOrigin(origins= "*")
    @GetMapping("/autofill")
    @ResponseBody
    public Mono<String> getLocationAutofill(@RequestParam(name = "text") String subtext){

        return haloClientInstance.getData("/autofill?text="+subtext).bodyToMono(String.class);
    }
}
