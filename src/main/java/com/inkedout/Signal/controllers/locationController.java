package com.inkedout.Signal.controllers;


import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1")
public class locationController {

    @Value("${halo.url}")
    private String haloUrl;

    @CrossOrigin(originPatterns = "*")
    @GetMapping("/location/autofill")
    @ResponseBody
    public Mono<String> getLocationAutofill(@RequestParam(name = "text") String subtext){
        WebClientInstance webClient = new WebClientInstance(haloUrl+"/location/autofill?text="+subtext);

        return webClient.getData("");
    }

    @GetMapping("/helloworld")
    @ResponseBody
    public String getHelloWorld(){
        return "Hello World!";
    }
}
