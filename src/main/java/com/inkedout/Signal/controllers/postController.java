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

        String haloUrl = "/withinradius?lat=" + newReq.loc.lat + "&lng=" + newReq.loc.lng + "&radius=" + newReq.radius;


        log.info("Request:" + haloUrl);
        return haloClient.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {
            JSONArray locationList;
            try{
                locationList = new JSONArray(res);
            }catch (Exception e){
                return Mono.just("No locations found");
            }

            ArrayList<Location> locList = new ArrayList<>();
            for(int i = 0; i < locationList.length(); i++){
                JSONObject obj = locationList.getJSONObject(i);
                float lat = obj.getFloat("latitude");
                float lng = obj.getFloat("longitude");
                Location loc = new Location();
                loc.lng = lng;
                loc.lat = lat;
                locList.add(loc);
            }
            LocationRequest locations = new LocationRequest();
            locations.locations = locList;
            log.info("Halo res:" + locationList);
            return polvoClient.postData("/users/location", locations).bodyToMono(String.class).flatMap(userRes -> {
                log.info("Polvo res:" + userRes);
                JSONArray userList;
                try{
                    userList = new JSONArray(userRes);
                } catch (JSONException e) {
                    return Mono.just("No Users For Locations Found");
                }

                ArrayList<User> usersList = new ArrayList<>();
                for(int i = 0; i < userList.length(); i++){
                    JSONObject obj = userList.getJSONObject(i);
                    String id = obj.getString("id");
                    User user = new User();
                    user.id = id;
                    usersList.add(user);
                }
                UserRequest userReq = new UserRequest();
                userReq.ids = usersList;

                return nectarClient.postData("/posts/users", userReq).bodyToMono(String.class);
            });
        });
    }
}
