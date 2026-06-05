package com.inkedout.Signal.controllers;

import com.inkedout.Signal.services.JwtHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    TokenController(JwtHelper jwtHelper) {
        this.jwtHelper  = jwtHelper;
    }

    private final JwtHelper jwtHelper;

    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    @GetMapping("/long")
    @ResponseBody
    public ResponseEntity<String> refreshToken(@RequestParam(name="id") String id){
        log.info("Getting a long token");
        try{
            if(id != null){
                JSONObject resJson = new JSONObject();
                return new ResponseEntity<>(resJson.put("token", jwtHelper.CreateToken(id, "long")).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token, param is null", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Issue with profile image refresh: ", e);
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/short")
    @ResponseBody
    public ResponseEntity<String> getShortToken(@RequestParam(name="id") String id){
        log.info("Getting a short token");
        try{
            if(id != null){
                JSONObject resJson = new JSONObject();
                return new ResponseEntity<>(resJson.put("token", jwtHelper.CreateToken(id, "short")).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token, param is null", HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e) {
            log.error("Issue with profile image getShortToken: ", e);
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<String> getBothTokens(@RequestParam(name="id") String id){
        log.info("Getting both tokens");
        try{
            if(id != null){
                JSONObject resJson = new JSONObject();
                String lToken =  jwtHelper.CreateToken(id, "long");
                String sToken = jwtHelper.CreateToken(id, "short");
                resJson.put("long", lToken);
                resJson.put("short", sToken);
                JSONObject resJson2 = new JSONObject();
                return new ResponseEntity<>(resJson2.put("tokens", resJson).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token, param is null", HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e) {
            log.error("Issue with getBothTokens: ", e);
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/refresh/short")
    @ResponseBody
    public ResponseEntity<String> refreshShortToken(@RequestBody String tokenCont){
        log.info("Refreshing short token");
        try{
            String token = new JSONObject(tokenCont).getString("token");
            boolean verified = jwtHelper.VerifyToken(token);
            if(!verified){
                return new ResponseEntity<>("Failed to refresh token, token couldn't be verified", HttpStatus.BAD_REQUEST);
            }
            String userId = jwtHelper.GetTokenSub(token);
            if(userId != null){
                JSONObject resJson = new JSONObject();
                return new ResponseEntity<>(resJson.put("token", jwtHelper.CreateToken(userId, "short")).toString(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Failed to refresh token, user id not found on token", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.info("Error refreshing short lived token{}", e.getMessage());
            return new ResponseEntity<>("Failed to refresh token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
