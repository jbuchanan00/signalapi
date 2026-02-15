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
        this.id = obj.getString("id");
        this.firstName = obj.getString("first_name");
        this.lastName = obj.getString("last_name");
        this.email = obj.getString("email");
        this.username = obj.optString("username");
        this.avatarExtension = obj.optString("avatar_extension");
        this.bio = obj.getString("bio");
        this.shopId = obj.optString("shop_id");
        this.roleId = obj.getInt("role_id");
        JSONObject locObj = obj.getJSONObject("location");
        this.location = new Location();
        this.location.lat = locObj.getFloat("x");
        this.location.lng = locObj.getFloat("y");
    }
}