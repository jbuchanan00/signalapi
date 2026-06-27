package com.inkedout.Signal.controllers;
import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.JwtHelper;
import com.inkedout.Signal.services.LogPersistence;
import com.inkedout.Signal.services.PolvoClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.jspecify.annotations.NonNull;
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
    UserController(PolvoClient polvoClient, JwtHelper jwtHelper,  LogPersistence logPersistence) {
        this.polvoClientInstance = polvoClient.polvoInstance;
        this.jwtHelper = jwtHelper;
        this.logPersistence = logPersistence;
    }

    private final JwtHelper jwtHelper;

    private final WebClientInstance polvoClientInstance;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final LogPersistence logPersistence;

    @GetMapping("/id")
    @ResponseBody
    public Mono<ResponseEntity<String>> getUserById(@RequestParam(name="id") String userId) {
        log.info("Getting user by id:{}", userId);
        try{
            return polvoClientInstance.getData("/users/ids?id=" + userId).bodyToMono(String.class)
                    .map(res ->
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
                        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
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
                        return handleTokenAddOn(res);
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
                        return handleTokenAddOn(res);
                    })
                    .onErrorResume(err -> Mono.just(new ResponseEntity<>(err.getMessage(), HttpStatus.BAD_REQUEST)));
        } catch (Exception e) {
            log.error("Issue with google auth: {}", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @NonNull
    private ResponseEntity<String> handleTokenAddOn(String res) {
        JSONObject userObj = new JSONObject(res).getJSONObject("user");
        String userId = userObj.getString("id");
        String shortJwt = null;
        String longJwt = null;
        try {
            shortJwt = jwtHelper.CreateToken(userId, "short");
            longJwt = jwtHelper.CreateToken(userId, "long");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        JSONObject tokens = new JSONObject().put("short", shortJwt).put("long", longJwt);
        JSONObject authObj = new JSONObject().put("user", userObj).put("tokens", tokens);
        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
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

    @PostMapping("/self")
    @ResponseBody
    public Mono<ResponseEntity<String>> getSelf(@RequestBody String req){
        log.info("Using token to extract user {}", req);
        try{
            JSONObject tokObj = new JSONObject(req).getJSONObject("token");
            String shortTok =  tokObj.optString("short");
            String longTok = tokObj.optString("long");
            if(shortTok == null && longTok == null){
                return  Mono.just(new ResponseEntity<>("No tokens in message", HttpStatus.BAD_REQUEST));
            }
            String token = shortTok != null ? shortTok : longTok;

            boolean goodTok = jwtHelper.VerifyToken(token);
            if(!goodTok){
                return Mono.just(new ResponseEntity<>("Bad Token", HttpStatus.BAD_REQUEST));
            }

            String payload = jwtHelper.GetTokenSub(token);
            JSONObject payloadObj = new JSONObject(payload);
            JSONObject sub = payloadObj.getJSONObject("sub");
            String userId = sub.getString("userid");
            return polvoClientInstance.getData("/users/ids?id=" + userId).bodyToMono(String.class)
                    .map(res -> {
                        try{
                            JSONArray user = new JSONArray(res);
                            if(user.isEmpty()){
                                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                            }
                            log.info("User response from polvo {}", user.toString());
                            JSONObject userObj = user.getJSONObject(0);
                            String newToken = null;
                            if(shortTok == null || shortTok.isEmpty()) {
                                newToken = jwtHelper.CreateToken(userObj.getString("id"), "short");
                            }else if(longTok == null || longTok.isEmpty()) {
                                newToken = token;
                            }
                            JSONObject newTokenObj = new JSONObject().put("short", newToken);
                            userObj.put("token", newTokenObj);
                            log.info("User Object to be sent {}", userObj.toString());
                            return new ResponseEntity<>(user.toString(), HttpStatus.OK);
                        }catch(Exception e){
                            Log newLog = new Log();
                            newLog.setMessage(e.getMessage());
                            newLog.setLevel(3);
                            logPersistence.save(newLog);
                            return new ResponseEntity<>("Couldn't return user with new token", HttpStatus.BAD_REQUEST);
                        }
                    });
        }catch(Exception e){
            Log newLog = new Log();
            newLog.setMessage(e.getMessage());
            newLog.setLevel(4);
            logPersistence.save(newLog);
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
