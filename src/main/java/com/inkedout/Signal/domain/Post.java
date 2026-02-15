package com.inkedout.Signal.domain;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    public String id;
    public Date createdAt;
    public Date updatedAt;
    public int role;
    public int mediaType;
    public String body;
    public String userId;
    public ArrayList<Tag> tags;
}
