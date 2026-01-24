package com.inkedout.Signal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Location {
    @Id
    private String id;

    public Location(String name, String state, float latitude, float longitude, int ranking){
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ranking = ranking;
    }

    public final String name;
    public final String state;
    public final float latitude;
    public final float longitude;
    public final int ranking;

    public Location() {
        this.name = "";
        this.state = "";
        this.latitude = 0.00F;
        this.longitude = 0.00F;
        this.ranking = 5;
    }

}
