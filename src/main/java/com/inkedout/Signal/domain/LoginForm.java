package com.inkedout.Signal.domain;

import org.json.JSONObject;

public class LoginForm {
    public String email;
    public String password;

    public JSONObject request(){
        return new JSONObject().append("form", this);
    }

    public LoginForm(String email, String password){}

    public void toJson(JSONObject request){
        this.email = request.optString("email");
        this.password = request.optString("password");
    }
}
