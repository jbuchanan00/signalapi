package com.inkedout.Signal.controllers;

import ch.qos.logback.core.rolling.helper.TokenConverter;
import com.google.gson.Gson;
import com.inkedout.Signal.services.JwtHelper;
import io.jsonwebtoken.Jwt;
import org.apache.el.parser.Token;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.GsonEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    TokenController(JwtHelper jwtHelper) {
        this.jwtHelper  = jwtHelper;
    }

    private JwtHelper jwtHelper;

    @PostMapping("/refresh/long")
    @ResponseBody
    public ResponseEntity<String> refreshToken(@RequestBody String tokenCont){
        try{
            String token = new JSONObject(tokenCont).getString("token");

            String userId = jwtHelper.GetTokenSub(token);
            if(userId != null){
                JSONObject resJson = new JSONObject();
                return new ResponseEntity<>(resJson.put("token", jwtHelper.CreateToken(userId, "long")).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {

            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/refresh/short")
    @ResponseBody
    public ResponseEntity<String> refreshShortToken(@RequestBody String tokenCont){
        try{
            String token = new JSONObject(tokenCont).getString("token");
            String userId = jwtHelper.GetTokenSub(token);
            if(userId != null){
                JSONObject resJson = new JSONObject();
                return new ResponseEntity<>(resJson.put("token", jwtHelper.CreateToken(userId, "short")).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("Error refreshing short lived token" + e.getMessage());
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
