package com.inkedout.Signal.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class locationController {


    @GetMapping("/location/autofill")
    @ResponseBody
    public String getLocationAutofill(@RequestParam(name = "text") String subtext){
            return subtext;
    }
}
