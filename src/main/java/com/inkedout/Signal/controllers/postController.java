package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

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

    @CrossOrigin(origins = "*")
    @PostMapping(value="/search")
    @ResponseBody
    public Mono<String> getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl);
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        WebClientInstance nectarClient = new WebClientInstance(nectarUrl);

        String haloUrl = "/withinradius?lat=" + newReq.loc.latitude + "&lng=" + newReq.loc.longitude + "&radius=" + newReq.radius;


        log.info("Request:" + haloUrl);
        return haloClient.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {

            JSONArray locationList = new JSONArray(res);
            ArrayList<Location> locList = new ArrayList<>();
            for(int i = 0; i < locationList.length(); i++){
                JSONObject obj = locationList.getJSONObject(i);
                float lat = obj.getFloat("latitude");
                float lng = obj.getFloat("longitude");
                Location loc = new Location();
                loc.longitude = lng;
                loc.latitude = lat;
                locList.add(loc);
            }
            ArrayList<Object> genericList = new ArrayList<>(locList);
            log.info("Halo res:" + locationList);
            return nectarClient.getData("/posts/users?user=user-1").bodyToMono(String.class);
//            return polvoClient.getData("/users/location?loc=-111.876183,40.758701").bodyToMono(String.class).flatMap(userRes -> {
//                log.info("Polvo res:" + userRes);
////                JsonNode userNode = mapper.readTree(userRes);
////                ArrayList<UserRequest> userList = new ArrayList<>();
////                for (JsonNode user : userNode) {
////                    UserRequest id = new UserRequest();
////                    id.id = String.valueOf(user.findValue("id"));
////                    userList.add(id);
////                }
//
//                return nectarClient.postData("/posts/users", new ArrayList<>()).bodyToMono(Post[].class);
//            });
        });
    }
}
