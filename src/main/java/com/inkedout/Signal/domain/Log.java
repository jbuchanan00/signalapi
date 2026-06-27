package com.inkedout.Signal.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Getter
@Setter
@Entity(name = "Log")
@Table(name = "log", schema = "public")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String message;

    private Integer level;

    private Timestamp createdAt;

    public Log() {
        id = null;
        message = null;
        level = null;
        createdAt = Timestamp.from(Instant.now());
    }

    public static Log fromJson(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            String message = jsonObject.getString("message");
            int level = jsonObject.getInt("level");
            Timestamp createdAt = Timestamp.from(Instant.now());
            Log log = new Log();
            log.message = message;
            log.level = level;
            log.createdAt = createdAt;
            return log;
        }catch(Exception e){
            log.error("Error creating log {}",  e.getMessage());
        }

        return new Log();
    }
}