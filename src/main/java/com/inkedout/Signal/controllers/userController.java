package com.inkedout.Signal.controllers;

import com.inkedout.Signal.domain.*;
import com.inkedout.Signal.services.WebClientInstance;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/users")
public class userController {
    @Value("${polvo.url}")
    private String polvoUrl;



    @GetMapping("/id")
    @ResponseBody
    public Mono<String> getUserById(@RequestParam(name="id", required = true) String userId){
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        String requestUrl = polvoUrl + "/" + userId;
        try{
            return polvoClient.getData(requestUrl).bodyToMono(String.class);
        }catch (Error e){
            log.error("Issue: " + e.getMessage());
            return Mono.just("Error getting user by id");
        }
    }

    @GetMapping("/email")
    @ResponseBody
    public Mono<String> getUserByEmail(@RequestParam(name="email", required = true) String email){
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
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
    public Mono<String> nativeRegisterUser(@RequestBody RegisterForm req){
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        try{
            return polvoClient.postData("/welcome/auth/register", req).bodyToMono(String.class)
                    .flatMap(_ -> Mono.just("Success"))
                    .onErrorResume(e -> Mono.just("Error registering: " + e.getMessage()));
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return Mono.just("Something is terribly terribly wrong: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Mono<String> nativeLoginUser(@RequestBody LoginForm req){
        log.info(polvoUrl + "/welcome/auth/login");
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        try{
            return polvoClient.postData("/welcome/auth/login", req)
                    .bodyToMono(String.class)
                    .flatMap( _ -> Mono.just("Success"))
                    .onErrorResume(e -> {
                        log.info("Error Logging in: " + e.getMessage());
                        return Mono.just("Failed");
                    });
        }catch(Error e) {
            log.error("Issue: ", e.getMessage());
            return Mono.just("Something broke :/");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editUserProfile(@RequestBody EditRequest req){
        WebClientInstance polvoClient = new WebClientInstance(polvoUrl);
        try{
            polvoClient.postData("/edit", req);
            return ResponseEntity.ok("Saved edit");
        }catch(Error e){
            log.error("Issue: ", e.getMessage());
            return ResponseEntity.ok("Failed to save");
        }
    }
}
