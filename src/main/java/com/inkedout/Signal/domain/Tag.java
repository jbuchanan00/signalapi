package com.inkedout.Signal.domain;

import org.json.JSONObject;

public class Tag {
    public String name;
    public String postId;

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("tag_name", name);
        obj.put("post_id", postId);
        return obj;
    };
}
