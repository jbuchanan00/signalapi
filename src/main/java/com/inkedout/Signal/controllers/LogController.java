package com.inkedout.Signal.controllers;


import com.inkedout.Signal.domain.Log;
import com.inkedout.Signal.services.LogPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {
    LogController(LogPersistence logPersistence) {
        this.logPersistence = logPersistence;
    }

    private final LogPersistence logPersistence;

    @PostMapping("/save")
    @ResponseBody
    public Mono<ResponseEntity<String>> save(@RequestBody String logReq) {
        Log logObj = Log.fromJson(logReq);
        try{
            logPersistence.save(logObj);
        }catch(Exception e){
            log.info("Exception in LogController.save", e);
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }
}
