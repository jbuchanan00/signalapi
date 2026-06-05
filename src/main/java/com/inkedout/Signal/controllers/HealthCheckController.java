package com.inkedout.Signal.controllers;

import com.sun.net.httpserver.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/check")
    public ResponseEntity<Authenticator.Success> getLocationAutofill(){
        log.info("Healthcheck");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
