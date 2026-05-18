package com.inkedout.Signal.domain;

import org.json.JSONObject;

public class SearchPostResponse {
    public Post post;
    public User user;

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("user", user.toJson());
        obj.put("post", post.toJson());
        return obj;
    }
}
