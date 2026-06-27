package com.inkedout.Signal.repositories;


import com.inkedout.Signal.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepo extends JpaRepository<Log, Integer> {
}
