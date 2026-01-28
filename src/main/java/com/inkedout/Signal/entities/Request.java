package com.inkedout.Signal.entities;


public class Request {

    public final Location loc;
    public final int radius;

    public Request(Location loc, int radius){
        this.loc = loc;
        this.radius = radius;
    }
}
