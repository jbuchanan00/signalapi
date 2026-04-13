package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.HaloClient;
import com.inkedout.Signal.services.NectarClient;
import com.inkedout.Signal.services.PolvoClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostController(PolvoClient polvoClient, NectarClient nectarClient, HaloClient haloClient){
        polvoClientInstance = polvoClient.polvoInstance;
        nectarClientInstance = nectarClient.nectarInstance;
        haloClientInstance = haloClient.haloInstance;
    }

    private final WebClientInstance polvoClientInstance;
    private final WebClientInstance nectarClientInstance;
    private final WebClientInstance haloClientInstance;

    @CrossOrigin(origins = "*")
    @PostMapping(value="/search")
    @ResponseBody
    public Mono<ResponseEntity<String>> getPostsForRequest(@RequestBody HomePostRequest newReq){
        String haloUrl = "/calculate?lat=" + newReq.loc.lat + "&long=" + newReq.loc.lng + "&radius=" + newReq.radius;

        log.info("Request:" + haloUrl);
        return haloClientInstance.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {
            JSONObject coordRange = new JSONObject(res);
            CoordRange coords = new CoordRange();
            try{
                coords.maxLat = coordRange.getFloat("MaxLat");
                coords.minLat = coordRange.getFloat("MinLat");
                coords.maxLong = coordRange.getFloat("MaxLong");
                coords.minLong = coordRange.getFloat("MinLong");
            }catch(Exception e){
                log.info("Error with calculate response" + e.getMessage());
                return Mono.just(new ResponseEntity<>("Error with Halo's response", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            CoordRangeRequest coordReq = new CoordRangeRequest();
            coordReq.coords = coords;
            log.info("Halo res:" + coords);
            return polvoClientInstance.postData("/users/location", coordReq).bodyToMono(String.class).flatMap(userRes -> {
                log.info("Polvo res:" + userRes);
                JSONArray userListJSON;
                try{
                    userListJSON = new JSONArray(userRes);
                } catch (JSONException e) {
                    return Mono.just(new ResponseEntity<>("No Users For Locations Found", HttpStatus.NO_CONTENT));
                }
                ArrayList<SearchPostResponse> responseToSend = new ArrayList<>();
                ArrayList<UserId> usersIdList = new ArrayList<>();
                ArrayList<User> userList = new ArrayList<>();
                for(int i = 0; i < userListJSON.length(); i++){
                    JSONObject obj = userListJSON.getJSONObject(i);
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
                return nectarClientInstance.postData("/posts/users", userReq).bodyToMono(String.class).flatMap(postRes -> {
                    log.info("Nectar res:" + postRes);
                    JSONArray postList;
                    try{
                        postList = new JSONArray(postRes);
                    }catch(JSONException e){
                        return Mono.just(new ResponseEntity<>("Issue with getting posts", HttpStatus.INTERNAL_SERVER_ERROR));
                    }

                    if(postList.isEmpty() || postList.toList().contains("No Ids in body")){
                        return  Mono.just(new ResponseEntity<>("No Posts Found", HttpStatus.NO_CONTENT));
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

                    return Mono.just(new ResponseEntity<>(JSONObject.valueToString(responseToSend), HttpStatus.OK));
                });
            });
        });
    }

    @ResponseBody
    @GetMapping("/artist")
    public Mono<ResponseEntity<String>> getPostsForArtist(@RequestParam(name="id") String artistId){


        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }
}
