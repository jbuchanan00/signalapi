package com.inkedout.Signal.domain;

public class RegisterRequest {
    public RegisterForm form;

    static public RegisterRequest fromForm(RegisterForm form){
        return new RegisterRequest(form);
    };

    RegisterRequest(RegisterForm form){
        this.form = form;
    }
}

