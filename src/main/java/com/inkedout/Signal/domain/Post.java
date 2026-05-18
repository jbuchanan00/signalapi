package com.inkedout.Signal.domain;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Post {
    public String id;
    public String createdAt;
    public String updatedAt;
    public int role;
    public int mediaType;
    public String body;
    public String userId;
    public ArrayList<Tag> tags;
    public String imageId;
    public String source;

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("createdAt", createdAt);
        obj.put("updatedAt", updatedAt);
        obj.put("role", role);
        obj.put("mediaType", mediaType);
        obj.put("body", body);
        obj.put("userId", userId);
        ArrayList<JSONObject> listOfTags = tags.stream().map(Tag::toJson).collect(Collectors.toCollection(ArrayList::new));
        obj.put("tags", new JSONArray(listOfTags));
        obj.put("imageId", imageId);
        obj.put("source", source);

        return obj;
    }

    public void convertFromJSON(JSONObject obj){
        this.id = obj.getString("id");
        this.createdAt = obj.getString("created_at");
        this.updatedAt = obj.optString("updated_at");
        this.role = obj.optInt("role_id");
        this.mediaType = obj.optInt("media_type_id");
        this.body = obj.optString("body");
        this.userId = obj.getString("user_id");
        this.imageId = obj.getString("image_id");
        this.source = obj.optString("source");
        JSONArray tagsExtract = obj.getJSONArray("tags");
        this.tags = new ArrayList<>();
        for(int i = 0; i < tagsExtract.length(); i++){
            JSONObject extractedTag = tagsExtract.getJSONObject(i);
            Tag formattedTag = new Tag();
            formattedTag.postId = extractedTag.getString("post_id");
            formattedTag.name = extractedTag.getString("tag_name");
            this.tags.add(formattedTag);
        }
    }
}
