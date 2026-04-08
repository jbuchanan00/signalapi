package com.inkedout.Signal.domain;

import com.google.gson.Gson;

public class StatusResponse {
    String status;

    public StatusResponse(String status){}

    public String convertToJson(){
        return new Gson().toJson(this);
    }
}
