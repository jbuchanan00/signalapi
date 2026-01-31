package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    @PostMapping("/search")
    @ResponseBody
    public Post[] getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl);
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        WebClientInstance nectarClient = new WebClientInstance(nectarUrl);
        Mono<Location[]> haloRes = haloClient.getData("/withinradius?lat=" + newReq.loc.latitude + "&lng=" + newReq.loc.longitude + "&radius" + newReq.radius).bodyToMono(Location[].class);
        Location[] locList = haloRes.block();
        log.info("Halo Result---", haloRes);
        assert locList != null;
        Mono<User[]> polvoRes = polvoClient.postData("/users/location", (RequestBody) Arrays.asList(locList)).bodyToMono(User[].class);
        User[] userList = polvoRes.block();
        assert userList != null;
        Stream<Object> userIdList = Arrays.stream(userList).map(user -> {
            UserRequest id = new UserRequest();
            id.id = user.id;
            return id;
        });
        log.info("Polvo Result---", polvoRes);
        Mono<Post[]> postRes = nectarClient.postData("/posts/users", (RequestBody) userIdList).bodyToMono(Post[].class);
        Post[] postList = postRes.block();
        log.info("Post Result---", postRes);

        return postList;
    }
}
