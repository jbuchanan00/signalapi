package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @CrossOrigin(origins = "*")
    @PostMapping(value="/search")
    @ResponseBody
    public Mono<String> getPostsForRequest(@RequestBody Request newReq){
        WebClientInstance haloClient = new WebClientInstance(haloUrl);
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        WebClientInstance nectarClient = new WebClientInstance(nectarUrl);

        String haloUrl = "/calculate?lat=" + newReq.loc.lat + "&long=" + newReq.loc.lng + "&radius=" + newReq.radius;

        log.info("Request:" + haloUrl);
        return haloClient.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {
            JSONObject coordRange = new JSONObject(res);
            CoordRange coords = new CoordRange();
            try{
                coords.maxLat = coordRange.getFloat("MaxLat");
                coords.minLat = coordRange.getFloat("MinLat");
                coords.maxLong = coordRange.getFloat("MaxLong");
                coords.minLong = coordRange.getFloat("MinLong");
            }catch(Exception e){
                log.info("Error with calculate response" + e.getMessage());
                return Mono.just("Error with Halo's response");
            }
            CoordRangeRequest coordReq = new CoordRangeRequest();
            coordReq.coords = coords;
            log.info("Halo res:" + coords);
            return polvoClient.postData("/users/location", coordReq).bodyToMono(String.class).flatMap(userRes -> {
                log.info("Polvo res:" + userRes);
                JSONArray userList;
                try{
                    userList = new JSONArray(userRes);
                } catch (JSONException e) {
                    return Mono.just("No Users For Locations Found");
                }

                ArrayList<UserId> usersList = new ArrayList<>();
                for(int i = 0; i < userList.length(); i++){
                    JSONObject obj = userList.getJSONObject(i);
                    String id = obj.getString("id");
                    UserId user = new UserId();
                    user.id = id;
                    usersList.add(user);
                }
                UserRequest userReq = new UserRequest();
                userReq.ids = usersList;
                return nectarClient.postData("/posts/users", userReq).bodyToMono(String.class).flatMap(postRes -> {
                    log.info("Nectar res:" + postRes);
                    JSONArray postList;
                    try{
                        postList = new JSONArray(postRes);
                    }catch(JSONException e){
                        return Mono.just("Issue with getting posts");
                    }

                    return Mono.just("");
                });
            });
        });
    }
}
