package com.inkedout.Signal.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class JwtHelper {

    JwtHelper(ObjectMapper objectMapper, @Value("${jwt.secret}") String jwtSecret) {
        this.objectMapper = objectMapper;
        this.JWTSECRET = jwtSecret;
    }

    private ObjectMapper objectMapper;

    private final String JWTSECRET;

    private static Logger log = LoggerFactory.getLogger(JwtHelper.class);

    final int SHORT_EXP = 60 * 60 * 1000;
    final long LONG_EXP = (6L * 30 * 24 * 60 * 60 * 1000);
    final String header = new JSONObject().put("alg", "HS256").put("typ", "JWT").toString();
    public boolean VerifyToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] strings = token.split("\\.");
        String signature = strings[2];
        String header =  strings[0];
        String payload = strings[1];
        String combined = header + "." + payload;
        log.info("Combined header: {}",  combined);
        Mac hash = javax.crypto.Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(JWTSECRET.getBytes(), "HmacSHA256");
        hash.init(key);
        java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        byte[] result = hash.doFinal(combined.getBytes());
//        log.info("Hash result: {} vs {}", new String(encoder.encode(result), StandardCharsets.UTF_8), signature);
        return new String(encoder.encode(result), StandardCharsets.UTF_8).equals(signature);
    }

    public String CreateToken(String userId, String exp) throws NoSuchAlgorithmException, InvalidKeyException {
        log.info("Creating token for userId {} and exp {}", userId, exp);
        long expMilli;
        if(exp.equals("short")){
            expMilli = SHORT_EXP;
        }else if(exp.equals("long")){
            expMilli = LONG_EXP;
        }else{
            return null;
        }
        JSONObject map = new JSONObject();
        JSONObject payload = new JSONObject();
        payload.put("userid", userId);
        map.put("sub", payload);
        map.put("exp", expMilli + System.currentTimeMillis());
        String tokenJson = map.toString();
        java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        byte[] headerEncoded = encoder.encode(header.getBytes());
        byte[] bodyEncoded = encoder.encode(tokenJson.getBytes());
        String combined = new String(headerEncoded, StandardCharsets.UTF_8) + "." + new String(bodyEncoded, StandardCharsets.UTF_8);
        Mac hash = javax.crypto.Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(JWTSECRET.getBytes(), "HmacSHA256");
        hash.init(key);
        byte[] result = hash.doFinal(combined.getBytes());
        return new String(headerEncoded, StandardCharsets.UTF_8) + "." + new String(bodyEncoded, StandardCharsets.UTF_8) + "." + new String(encoder.encode(result), StandardCharsets.UTF_8);
    }

    public String GetTokenSub(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] strings = token.split("\\.");
        String payload = strings[1];
        java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
        byte[] payloadDecoded = decoder.decode(payload.getBytes());
        if(VerifyToken(token)){
            return new String(payloadDecoded, StandardCharsets.UTF_8);
        }else{
            return "Token could not be verified";
        }
    }

}
