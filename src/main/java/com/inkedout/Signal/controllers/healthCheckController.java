package com.inkedout.Signal.controllers;

import com.sun.net.httpserver.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/health")
public class healthCheckController {


    @GetMapping("/check")
    public ResponseEntity<Authenticator.Success> getLocationAutofill(){

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
