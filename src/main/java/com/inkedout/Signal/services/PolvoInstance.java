package com.inkedout.Signal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class PolvoInstance {
    @Value("${polvo.url}")
    private String polvoUrl;

    private static final AtomicReference<PolvoInstance> INSTANCE = new AtomicReference<PolvoInstance>();

    public PolvoInstance(){
        final PolvoInstance previous = INSTANCE.getAndSet(this);
        if(previous != null){
            throw new IllegalStateException("Second singleton " + this + " created after " + previous);
        }
    }

    public static PolvoInstance getInstance(){
        return INSTANCE.get();
    }
}
