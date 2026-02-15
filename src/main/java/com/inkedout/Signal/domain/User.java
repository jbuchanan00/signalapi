package com.inkedout.Signal.domain;

import org.json.JSONObject;

public class User {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String username;
    public String avatarExtension;
    public int roleId;
    public Location location;
    public String bio;
    public String shopId;

    public void convertFromJSON(JSONObject obj){
        obj.getString("id");
        obj.getString("first_name");
        obj.getString("last_name");
        obj.getString("email");
        obj.getString("username");
        obj.getString("avatar_extension");
        obj.getString("bio");
        obj.getString("shopId");
        obj.getInt("roleId");
        
    }
}