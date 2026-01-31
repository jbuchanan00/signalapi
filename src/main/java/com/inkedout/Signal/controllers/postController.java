package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.Location;
import com.inkedout.Signal.domain.Post;
import com.inkedout.Signal.domain.Request;
import com.inkedout.Signal.domain.User;
import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

import static reactor.netty.http.HttpConnectionLiveness.log;

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
    public Mono<Post[]> getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl);
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        WebClientInstance nectarClient = new WebClientInstance(nectarUrl);
        Mono<Location[]> haloRes = Mono.from(haloClient.getData("/withinradius?lat=" + newReq.loc.latitude + "&lng=" + newReq.loc.longitude + "&radius" + newReq.radius).bodyToMono(Location[].class));
        log.info("Halo Result---", haloRes);
        Mono<User[]> polvoRes = Mono.from(polvoClient.postData("/users/location", (RequestBody) haloRes).bodyToMono(User[].class));
        log.info("Polvo Result---", polvoRes);
        Mono<Post[]> postRes = Mono.from(nectarClient.postData("/posts/users", (RequestBody) polvoRes).bodyToMono(Post[].class));
        log.info("Post Result---", postRes);

        return postRes;
    }
}
