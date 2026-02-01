package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

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

//    @CrossOrigin(origins = "https://app.inked-out.com")
    @PostMapping(value="/search", headers="Content-Type=application/json")
    @ResponseBody
    public Mono<Post[]> getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl);
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        WebClientInstance nectarClient = new WebClientInstance(nectarUrl);

        String haloUrl = "/withinradius?lat=" + newReq.loc.latitude + "&lng=" + newReq.loc.longitude + "&radius=" + newReq.radius;


        log.info("Request:" + haloUrl);
        return haloClient.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode map = mapper.readTree(res);
            log.info("Halo res:" + res);
            return polvoClient.postData("/users/location", (RequestBody) map).bodyToMono(String.class).flatMap(userRes -> {
                JsonNode userNode = mapper.readTree(userRes);
                ArrayList<UserRequest> userList = new ArrayList<UserRequest>();
                for (JsonNode user : userNode) {
                    UserRequest id = new UserRequest();
                    id.id = String.valueOf(user.findValue("id"));
                    userList.add(id);
                }

                return nectarClient.postData("/posts/users", (RequestBody) userList).bodyToMono(Post[].class);
            });
        });
    }
}
