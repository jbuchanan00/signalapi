package com.inkedout.Signal.controllers;

import com.inkedout.Signal.services.JwtHelper;
import io.jsonwebtoken.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/token")
public class TokenController {

    @GetMapping("/refresh/long")
    @ResponseBody
    public ResponseEntity<String> refreshToken(@RequestParam(name="token") String token){
        try{
            String userId = new JwtHelper().GetTokenSub(token);
            if(userId != null){
                return new ResponseEntity<>(new JwtHelper().CreateToken(userId, "long"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {

            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/refresh/short")
    @ResponseBody
    public ResponseEntity<String> refreshShortToken(@RequestParam(name="token") String token){
        try{
            String userId = new JwtHelper().GetTokenSub(token);
            if(userId != null){
                return new ResponseEntity<>(new JwtHelper().CreateToken(userId, "short"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
