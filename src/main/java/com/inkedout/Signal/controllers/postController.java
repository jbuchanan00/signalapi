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
    public Mono<Object> getPostsForRequest(@RequestBody Request newReq){
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
                JSONArray userListJSON;
                try{
                    userListJSON = new JSONArray(userRes);
                } catch (JSONException e) {
                    return Mono.just("No Users For Locations Found");
                }
                ArrayList<SearchPostResponse> responseToSend = new ArrayList<>();
                ArrayList<UserId> usersIdList = new ArrayList<>();
                ArrayList<User> userList = new ArrayList<>();
                for(int i = 0; i < userListJSON.length(); i++){
                    JSONObject obj = userListJSON.getJSONObject(i);
//                    SearchPostResponse userDataForPost = new SearchPostResponse();
//                    userDataForPost.user = new User();
//                    userDataForPost.user.convertFromJSON(obj);
//                    responseToSend.add(userDataForPost);
                    User newUser = new User();
                    newUser.convertFromJSON(obj);
                    userList.add(newUser);
                    String id = obj.getString("id");
                    UserId user = new UserId();
                    user.id = id;
                    usersIdList.add(user);
                }
                UserRequest userReq = new UserRequest();
                userReq.ids = usersIdList;
                return nectarClient.postData("/posts/users", userReq).bodyToMono(String.class).flatMap(postRes -> {
                    log.info("Nectar res:" + postRes);
                    JSONArray postList;
                    try{
                        postList = new JSONArray(postRes);
                    }catch(JSONException e){
                        return Mono.just("Issue with getting posts");
                    }

                    if(postList.isEmpty() || postList.toList().contains("No Ids in body")){
                        return  Mono.just("No Posts Found");
                    }

                    for(int i = 0; i < postList.length(); i++){
                        JSONObject extractedPost = postList.getJSONObject(i);
                        Post postData = new Post();
                        postData.convertFromJSON(extractedPost);
                        SearchPostResponse searchPostResponse = new SearchPostResponse();
                        searchPostResponse.post = postData;
                        User dummyUser = new User();
                        dummyUser.id = postData.userId;
                        int j = userList.indexOf(dummyUser);
                        if(j > -1){
                            searchPostResponse.user = userList.get(j);
                        }
                        responseToSend.add(searchPostResponse);
                    }

                    return Mono.just(responseToSend);
                });
            });
        });
    }
}
