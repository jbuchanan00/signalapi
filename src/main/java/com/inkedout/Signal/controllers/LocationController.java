package com.inkedout.Signal.controllers;


import com.inkedout.Signal.services.HaloClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;



@RestController
@RequestMapping("/api/location")
public class LocationController {
    LocationController(HaloClient haloClient) {
        haloClientInstance = haloClient.haloInstance;
    }

    private final WebClientInstance haloClientInstance;
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @CrossOrigin(origins= "*")
    @GetMapping("/autofill")
    @ResponseBody
    public Mono<String> getLocationAutofill(@RequestParam(name = "text") String subtext){

        return haloClientInstance.getData("/autofill?text="+subtext).bodyToMono(String.class);
    }

    @GetMapping("/resolve")
    @ResponseBody
    public Mono<ResponseEntity<String>> getLocationResolve(@RequestParam(name = "latitude") String latitude, @RequestParam(name = "longitude") String longitude){
        log.info("Requesting Resolve Coords: {} {}", latitude, longitude);
        return haloClientInstance.getData("/resolveCoordinates?latitude=" + latitude + "&longitude=" + longitude).bodyToMono(String.class).map(
                res -> new ResponseEntity<>(res, HttpStatus.OK)
        ).onErrorResume(_ -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
    }
}
