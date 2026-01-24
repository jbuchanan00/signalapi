package com.inkedout.Signal.controllers;

import com.inkedout.Signal.entities.Request;
import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/posts")
public class postController {

    @Value("${halo.url}")
    private String haloUrl;
    @Value("${polvo.url}")
    private String polvoUrl;
    @Value("${nectar.url}")
    private String nectarUrl;

    @CrossOrigin(origins = "https://app.inked-out.com")
    @PostMapping("/get")
    @ResponseBody
    public String getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl+"/withinradius?lat=" + newReq.loc.latitude + "&lng=" + newReq.loc.longitude + "&radius" + newReq.radius);
        haloClient.getData("");

        return "Hello";
    }
}
