package com.inkedout.Signal.controllers;
import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.JwtHelper;
import com.inkedout.Signal.services.PolvoClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserController(PolvoClient polvoClient, JwtHelper jwtHelper) {
        this.polvoClientInstance = polvoClient.polvoInstance;
        this.jwtHelper = jwtHelper;
    }

    private final JwtHelper jwtHelper;

    private final WebClientInstance polvoClientInstance;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/id")
    @ResponseBody
    public Mono<ResponseEntity<String>> getUserById(@RequestParam(name="id") String userId) {
        log.info("Getting user by id:{}", userId);
        try{
            return polvoClientInstance.getData("/users/ids?id=" + userId).bodyToMono(String.class).map(res ->
                        new ResponseEntity<>(res, HttpStatus.OK)
                    ).onErrorResume(_ -> {
                        log.error("Error getting User by Id");
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    });
        }catch (Error e){
            log.error("Issue Getting User by Id: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/email")
    @ResponseBody
    public Mono<ResponseEntity<String>> getUserByEmail(@RequestParam(name="email") String email){
        log.info("Getting user by email:{}", email);
        String requestUrl = "/" + email;

        try{
            return polvoClientInstance.getData(requestUrl).bodyToMono(String.class).map(res ->
                        new ResponseEntity<>(res, HttpStatus.OK)
                    ).onErrorResume(_ -> {
                        log.error("Error getting User by Email");
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            });
        }catch (Error e){
            log.error("Issue Getting User By Email: {} ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Mono<ResponseEntity<String>> nativeRegisterUser(@RequestBody RegisterForm req){
        log.info("Creating a new user:{}", req);
        try{
            return polvoClientInstance.postData("/welcome/auth/register", req).bodyToMono(String.class)
                    .map(res -> {
                        log.info("Register return value {}", res);
                        JSONObject userObj = new JSONObject(res);
                        String userId = userObj.getString("id");
                        String shortJwt = null;
                        try {
                            shortJwt = jwtHelper.CreateToken(userId, "short");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        }
                        String longJwt = null;
                        try {
                            longJwt = jwtHelper.CreateToken(userId, "long");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        }
                        JSONObject tokens = new JSONObject().put("short", shortJwt).put("long", longJwt);
                        JSONObject authObj = new JSONObject().put("user", userObj).put("tokens", tokens);
                        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
                    })
                    .onErrorResume(err -> {
                        log.error("Error registering the user {}", err.getMessage());
                            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));});
        }catch(Error e){
            log.error("Issue Registering the user: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Mono<ResponseEntity<String>> nativeLoginUser(@RequestBody String req){
        log.info("Logging in user:{}", req);
        try{
            return polvoClientInstance.postData("/welcome/auth/login", req)
                    .bodyToMono(String.class)
                    .map( res -> {
                        JSONObject userObj = new JSONObject(res).getJSONObject("user");
                        String userId = userObj.getString("id");
                        String shortJwt = null;
                        try {
                            shortJwt = jwtHelper.CreateToken(userId, "short");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        }
                        JSONObject tokens = new JSONObject().put("short", shortJwt);
                        JSONObject authObj = new JSONObject().put("user", userObj).put("token", tokens);
                        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
                    })
                    .onErrorResume(e -> {
                        log.warn("Error Logging in: {}", e.getMessage());
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    });
        }catch(Error e) {
            log.error("Issue Logging in: {} ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public Mono<ResponseEntity<String>> editUserProfile(@RequestBody String req){
        log.info("Editing user:{}", req);
        try{
            return polvoClientInstance.postData("/edit", req).bodyToMono(String.class)
                .map(res ->
                {
                        return new ResponseEntity<>(res, HttpStatus.OK);}
                )
                .onErrorResume(e ->{
                            log.warn("There was an issue trying to edit the user profile{}", e.getMessage());
                        return Mono.just(ResponseEntity.badRequest().build());}
                    );
        }catch(Error e) {
            log.error("Issue Editing User: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/auth/google")
    @ResponseBody
    public Mono<ResponseEntity<String>> authGoogle(@RequestParam(name="code") String code){
        log.info("User Logging in with Google");
        try{
            return polvoClientInstance.getData("/auth/google/callback?code=" + code).bodyToMono(String.class)
                    .map(res -> {
                        log.info("Google Callback response {}", res);
                        JSONObject userObj = new JSONObject(res).getJSONObject("user");
                        String userId = userObj.getString("id");
                        String shortJwt = null;
                        try {
                            shortJwt = jwtHelper.CreateToken(userId, "short");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        }
                        JSONObject tokens = new JSONObject().put("short", shortJwt);
                        JSONObject authObj = new JSONObject().put("user", userObj).put("token", tokens);
                        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
                    })
                    .onErrorResume(err -> Mono.just(new ResponseEntity<>(err.getMessage(), HttpStatus.BAD_REQUEST)));
        } catch (Exception e) {
            log.error("Issue with google auth: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/image/extension")
    @ResponseBody
    public Mono<ResponseEntity<String>> imageExtension(@RequestBody String req){
        try{
            return polvoClientInstance.postData("/avatar", req).bodyToMono(String.class)
                    .map(res -> new ResponseEntity<>(res, HttpStatus.OK))
                    .onErrorResume(_ -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
        }catch(Error e) {
            log.error("Issue with profile image retrieval: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/user")
    @ResponseBody
    public Mono<ResponseEntity<String>> deleteUser(@RequestParam(name="id") String req){
        log.info("I am deleting the user here");
        try{
            return polvoClientInstance.getData("/delete/user?id=" + req).bodyToMono(String.class)
                    .map(res -> new ResponseEntity<>(res, HttpStatus.OK))
                    .onErrorResume(_ -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
        }catch(Exception e){
            return Mono.just(new ResponseEntity<>("Bad", HttpStatus.BAD_REQUEST));
        }
    }

    @GetMapping("/deleteme")
    @ResponseBody
    public Mono<ResponseEntity<String>> user(@RequestParam(name="id") String req){
        return Mono.just(new ResponseEntity<>(req, HttpStatus.OK));
    }
}
