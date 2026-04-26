package com.inkedout.Signal.services;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static reactor.netty.http.HttpConnectionLiveness.log;

public class JwtHelper {
    @Value("{jwt.secret}")
    String JWTSECRET;

    final int SHORT_EXP = 60 * 60 * 1000;
    final long LONG_EXP = (6L * 30 * 24 * 60 * 60 * 1000);

    public boolean VerifyToken(String token){
        try{
            Date expDate = Jwts.parserBuilder().setSigningKey(JWTSECRET.getBytes()).build().parseClaimsJwt(token).getBody().getExpiration();
            long diff = Math.abs(expDate.getTime() - new Date().getTime());
            return diff <= 1000 * 5;
        } catch (Exception e) {
           log.info("There was a problem with the token" + e.getMessage());
           return false;
        }
    }

    public String CreateToken(String userId, String exp){
        long expMilli;
        if(exp.equals("short")){
            expMilli = SHORT_EXP;
        }else if(exp.equals("long")){
            expMilli = LONG_EXP;
        }else{
            return null;
        }
        return Jwts.builder().claim("userId", userId).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expMilli)).signWith(
                Keys.hmacShaKeyFor(JWTSECRET.getBytes()),
                SignatureAlgorithm.HS256
        ).compact();
    }

    public String GetTokenSub(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(JWTSECRET.getBytes()).build().parseClaimsJwt(token).getBody().getSubject();
        }catch(Exception e){
            log.info("Token is bad" + e.getMessage());
            return null;
        }
    }

}
