package com.inkedout.Signal.services;


import com.inkedout.Signal.domain.Log;
import com.inkedout.Signal.repositories.LogRepo;
import org.springframework.stereotype.Service;

@Service
public class LogPersistence {
    private final LogRepo repo;

    public LogPersistence(LogRepo repo) {
        this.repo = repo;
    }

    public void save(Log log) {
        repo.save(log);
    }
}
