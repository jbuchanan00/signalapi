package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.repositories.ReportedCategoryRepo;
import com.inkedout.Signal.repositories.ReportingPostRepo;
import com.inkedout.Signal.services.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostController(PolvoClient polvoClient, NectarClient nectarClient, HaloClient haloClient, ReportedPostService reportedPostService, ReportedCategoryService reportedCategoryService) {
        polvoClientInstance = polvoClient.polvoInstance;
        nectarClientInstance = nectarClient.nectarInstance;
        haloClientInstance = haloClient.haloInstance;
        this.reportedPostService = reportedPostService;
        this.reportedCategoryService = reportedCategoryService;
    }

    private final WebClientInstance polvoClientInstance;
    private final WebClientInstance nectarClientInstance;
    private final WebClientInstance haloClientInstance;
    private final ReportedPostService reportedPostService;
    private final ReportedCategoryService reportedCategoryService;

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @CrossOrigin(origins = "*")
    @PostMapping(value="/search")
    @ResponseBody
    public Mono<ResponseEntity<String>> getPostsForRequest(@RequestBody HomePostRequest newReq){
        String haloUrl = "/calculate?lat=" + newReq.loc.lat + "&long=" + newReq.loc.lng + "&radius=" + newReq.radius;
        log.info("Getting Posts Request: {}", newReq.loc.lat + " " + newReq.loc.lng);
        long millis = System.currentTimeMillis();
        return haloClientInstance.getData(haloUrl).bodyToMono(String.class).flatMap(res -> {
            JSONObject coordRange = new JSONObject(res);
            CoordRange coords = new CoordRange();
            try{
                coords.convertFromJSON(coordRange);
            }catch(Exception e){
                log.warn("Error with calculate response{}", e.getMessage());
                return Mono.just(new ResponseEntity<>("Error with Halo's response", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            return polvoClientInstance.postData("/users/location", coords.request().toString()).bodyToMono(String.class).flatMap(userRes -> {
                JSONArray userListJSON;
                try{
                    userListJSON = new JSONArray(userRes);
                } catch (JSONException e) {
                    return Mono.just(new ResponseEntity<>("No Users For Locations Found", HttpStatus.NO_CONTENT));
                }
                ArrayList<UserId> usersIdList = new ArrayList<>();
                ArrayList<User> userList = new ArrayList<>();
                for(int i = 0; i < userListJSON.length(); i++){
                    JSONObject obj = userListJSON.getJSONObject(i);
                    User newUser = new User();
                    try{
                        newUser.convertFromJSON(obj);
                    }catch(Exception e){
                        log.error("Error with converting user to json{}", e.getMessage());
                        return Mono.just(new ResponseEntity<>("Error with Users response", HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                    userList.add(newUser);
                    String id = obj.getString("id");
                    UserId user = new UserId();
                    user.id = id;
                    usersIdList.add(user);
                }
                UserRequest userReq = new UserRequest();
                userReq.ids = usersIdList;
                return nectarClientInstance.postData("/posts/users", userReq).bodyToMono(String.class).flatMap(postRes -> {
                    JSONArray postList;
                    try{
                        postList = new JSONArray(postRes);
                    }catch(JSONException e){
                        return Mono.just(new ResponseEntity<>("Issue with getting posts", HttpStatus.INTERNAL_SERVER_ERROR));
                    }

                    if(postList.isEmpty() || postList.toList().contains("No Ids in body")){
                        return  Mono.just(new ResponseEntity<>("No Posts Found", HttpStatus.NO_CONTENT));
                    }
                    ArrayList<JSONObject> postListJSON = new ArrayList<>();
                    for(int i = 0; i < postList.length(); i++) {
                        JSONObject extractedPost = postList.getJSONObject(i);
                        Post postData = new Post();
                        postData.convertFromJSON(extractedPost);
                        SearchPostResponse searchPostResponse = new SearchPostResponse();
                        searchPostResponse.post = postData;
                        User dummyUser = new User();
                        dummyUser.id = postData.userId;
                        int j = userList.indexOf(dummyUser);
                        if (j > -1) {
                            searchPostResponse.user = userList.get(j);
                        }
                        postListJSON.add(searchPostResponse.toJson());
                    }
                    log.info("Successfully posted {} posts in:{}", postListJSON.size(), System.currentTimeMillis()-millis);
                    return Mono.just(new ResponseEntity<>(postListJSON.toString(), HttpStatus.OK));
                });
            });
        });
    }

    @ResponseBody
    @GetMapping("/artist")
    public Mono<ResponseEntity<String>> getPostsForArtist(@RequestParam(name="id") String artistId, @RequestParam(name="page") String page, @RequestParam(name="pagesize")  String pagesize){
        log.info("Getting Posts for Artist: {}", artistId);
        try{
            return nectarClientInstance.getData("/userposts?userId=" + artistId + "&page=" + page + "&pageSize=" + pagesize).bodyToMono(String.class)
                    .map(res -> new ResponseEntity<>(res, HttpStatus.OK))
                    .onErrorResume(err -> Mono.just(new ResponseEntity<>(err.getMessage(), HttpStatus.BAD_REQUEST)));
        }catch(Exception e){
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/new")
    @ResponseBody
    public Mono<ResponseEntity<String>> insertNewPost(@RequestBody String newPost){
        log.info("Creating a new post:{}", newPost);

        try{
            return nectarClientInstance.postData("/addpost", newPost).bodyToMono(String.class)
                    .map(res -> new ResponseEntity<>(res, HttpStatus.CREATED))
                    .onErrorResume(err -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
        }catch(Exception e){
            return  Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/report")
    @ResponseBody
    public Mono<ResponseEntity<String>> reportPost(@RequestBody String report){
        log.info("Reporting post:{}", report);
        try{
            ReportedPost post = new ReportedPost();
            JSONObject obj = new JSONObject(report);
            String categoryName = obj.getString("category");
            ReportedCategory reportedCategory = reportedCategoryService.getReportedCategory(categoryName);
            post.fromJson(report);
            post.setCategory(reportedCategory);
            reportedPostService.saveReportedPost(post);
            return Mono.just(new ResponseEntity<>(report, HttpStatus.OK));
        }catch(Exception e){
            log.info("Reported post is null");
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
    }
}
