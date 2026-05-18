package com.inkedout.Signal.domain;

import org.json.JSONObject;

public class Location {
    public float lat;
    public float lng;

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("lat", lat);
        json.put("lng", lng);

        return json;
    }
}
