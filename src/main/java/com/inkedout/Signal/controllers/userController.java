package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/users")
public class userController {
    @Value("${polvo.url}")
    private String polvoUrl;

    WebClientInstance polvoClient = new WebClientInstance(polvoUrl);

    @GetMapping("")
    @ResponseBody
    public Mono<String> getUserById(@RequestParam(name="id", required = true) String userId){
        String requestUrl = polvoUrl + "/" + userId;
        try{
            return polvoClient.getData(requestUrl).bodyToMono(String.class);
        }catch (Error e){
            log.error("Issue: " + e.getMessage());
            return Mono.just("Error getting user by id");
        }
    }

    @GetMapping("")
    @ResponseBody
    public Mono<String> getUserByEmail(@RequestParam(name="email", required = true) String email){
        String requestUrl = polvoUrl + "/" + email;

        try{
            return polvoClient.getData(requestUrl).bodyToMono(String.class);
        }catch (Error e){
            log.error("Issue: " + e.getMessage());
            return Mono.just("Error getting user by email");
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> nativeRegisterUser(@RequestBody RegisterForm req){
        try{
            polvoClient.postData("/welcome/auth/register", JSONObject.valueToString(req));
            return ResponseEntity.ok("Success");
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return ResponseEntity.ok("Failed");
        }

    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> nativeLoginUser(@RequestBody LoginForm req){
        try{
            polvoClient.postData("/welcome/auth/login", req);
            return ResponseEntity.ok("Success");
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return ResponseEntity.ok("Failed");
        }

    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editUserProfile(@RequestBody EditRequest req){
        try{
            polvoClient.postData("/edit", req);
            return ResponseEntity.ok("Saved edit");
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return ResponseEntity.ok("Failed to save");
        }
    }
}
