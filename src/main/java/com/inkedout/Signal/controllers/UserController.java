package com.inkedout.Signal.controllers;
import com.google.gson.JsonObject;
import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.JwtHelper;
import com.inkedout.Signal.services.PolvoClient;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static reactor.netty.http.HttpConnectionLiveness.log;

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

    @GetMapping("/id")
    @ResponseBody
    public Mono<ResponseEntity<String>> getUserById(@RequestParam(name="id") String userId) {

        try{
            return polvoClientInstance.getData("/users/ids?id=" + userId).bodyToMono(String.class).map(res ->
                        new ResponseEntity<>(res, HttpStatus.OK)
                    ).onErrorResume(_ -> {
                        log.error("Error getting User by Id");
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    });
        }catch (Error e){
            log.error("Issue: " + e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/email")
    @ResponseBody
    public Mono<ResponseEntity<String>> getUserByEmail(@RequestParam(name="email") String email){

        String requestUrl = "/" + email;

        try{
            return polvoClientInstance.getData(requestUrl).bodyToMono(String.class).map(res ->
                        new ResponseEntity<>(res, HttpStatus.OK)
                    ).onErrorResume(_ -> {
                        log.error("Error getting User by Email");
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            });
        }catch (Error e){
            log.error("Issue: " + e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Mono<ResponseEntity<String>> nativeRegisterUser(@RequestBody RegisterForm req){

        try{
            return polvoClientInstance.postData("/welcome/auth/register", req).bodyToMono(String.class)
                    .map(res -> {
                        JSONObject userObj = new JSONObject(res).getJSONObject("user");
                        String userId = userObj.getString("id");
                        String shortJwt = jwtHelper.CreateToken(userId, "short");
                        String longJwt = jwtHelper.CreateToken(userId, "long");
                        JSONObject tokens = new JSONObject().put("short", shortJwt).put("long", longJwt);
//                        JSONObject tokens = new JSONObject().append("short", shortJwt).append("long", longJwt);
                        JSONObject authObj = new JSONObject().put("user", userObj).put("tokens", tokens);
                        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
                    })
                    .onErrorResume(err -> {
                            log.error("Error getting User by Email " + err.getMessage());
                            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));});
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Mono<ResponseEntity<String>> nativeLoginUser(@RequestBody LoginForm req){

        try{
            return polvoClientInstance.postData("/welcome/auth/login", req)
                    .bodyToMono(String.class)
                    .map( res -> {
                        JSONObject userObj = new JSONObject(res).getJSONObject("user");
                        String userId = userObj.getString("id");
                        String shortJwt = jwtHelper.CreateToken(userId, "short");
                        String longJwt = jwtHelper.CreateToken(userId, "long");
                        JSONObject tokens = new JSONObject().append("short", shortJwt).append("long", longJwt);
                        JSONObject authObj = new JSONObject().append("user", userObj).append("tokens", tokens);
                        return new ResponseEntity<>(authObj.toString(), HttpStatus.OK);
                    })
                    .onErrorResume(e -> {
                        log.info("Error Logging in: " + e.getMessage());
                        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    });
        }catch(Error e) {
            log.error("Issue: ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public Mono<ResponseEntity<String>> editUserProfile(@RequestBody EditRequest req){

        try{
            return polvoClientInstance.postData("/edit", req).bodyToMono(String.class)
                .map(_ -> new ResponseEntity<String>(HttpStatus.OK)).onErrorResume(_ ->
                        Mono.just(ResponseEntity.badRequest().build())
                    );
        }catch(Error e) {
            log.error("Issue: ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/auth/google")
    @ResponseBody
    public Mono<ResponseEntity<String>> authGoogle(@RequestParam(name="code") String code){
        try{
            return polvoClientInstance.getData("/auth/google/callback?code=" + code).bodyToMono(String.class)
                    .map(res -> new ResponseEntity<>(res, HttpStatus.OK))
                    .onErrorResume(_ -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));
        } catch (Exception e) {
            log.error("Issue: ", e.getMessage());
            return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
