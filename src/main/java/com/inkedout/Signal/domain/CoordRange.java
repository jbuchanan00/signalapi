package com.inkedout.Signal.domain;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static reactor.netty.http.HttpConnectionLiveness.log;

public class CoordRange extends JSONObject {
    public float minLat;
    public float maxLat;
    public float minLong;
    public float maxLong;

    public JSONObject request(){
        return new JSONObject().put("coords", this.convertToJSON());
    }

    public void convertFromJSON(JSONObject obj){
        log.info("Converting json " + obj);
        this.maxLat = obj.getFloat( "MaxLat");
        this.minLat = obj.getFloat("MinLat");
        this.maxLong = obj.getFloat("MaxLong");
        this.minLong = obj.getFloat("MinLong");
    }

    public JSONObject convertToJSON(){
        JSONObject request = new JSONObject();
        request.put("MaxLat", maxLat);
        request.put("MinLat", minLat);
        request.put("MaxLong", maxLong);
        request.put("MinLong", minLong);
        return request;
    }
}

