package com.inkedout.Signal.domain;

import com.google.gson.Gson;
import org.json.JSONObject;

public class CoordRange extends JSONObject {
    public float minLat;
    public float maxLat;
    public float minLong;
    public float maxLong;

    public JSONObject request(){
        return new JSONObject().append("coords", this);
    }

    public void convertFromJSON(JSONObject obj){
        this.maxLat = obj.getFloat("MaxLat");
        this.minLat = obj.getFloat("MinLat");
        this.maxLong = obj.getFloat("MaxLong");
        this.minLong = obj.getFloat("MinLong");
    }
}

