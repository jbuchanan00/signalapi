package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class HaloInstance {
    @Value("${halo.url}")
    private String haloUrl;

    public WebClientInstance halo;

    private static final AtomicReference<HaloInstance> INSTANCE = new AtomicReference<>();

    public HaloInstance(){
        final HaloInstance previous = INSTANCE.getAndSet(this);
        halo = new WebClientInstance(haloUrl);
        if(previous != null){
            throw new IllegalStateException("Second singleton " + this + " created after " + previous);
        }
    }

    public static HaloInstance getInstance(){
        return INSTANCE.get();
    }
}
