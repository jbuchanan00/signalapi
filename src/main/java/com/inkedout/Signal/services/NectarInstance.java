package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class NectarInstance {
    @Value("${nectar.url}")
    private String nectarUrl;

    private static final AtomicReference<NectarInstance> INSTANCE = new AtomicReference<NectarInstance>();

    public NectarInstance(){
        final NectarInstance previous = INSTANCE.getAndSet(this);
        if(previous != null){
            throw new IllegalStateException("Second singleton " + this + " created after " + previous);
        }
    }

    public static NectarInstance getInstance(){
        return INSTANCE.get();
    }
}
