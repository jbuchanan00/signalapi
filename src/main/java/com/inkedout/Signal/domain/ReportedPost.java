package com.inkedout.Signal.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reported_post", schema = "public")
@Data
public class ReportedPost {
    private static final Logger log = LoggerFactory.getLogger(ReportedPost.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "comment", length = Integer.MAX_VALUE)
    @Getter @Setter
    private String comment;

    @Column(name = "reporter_id")
    private UUID reporterId;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private ReportedCategory category;

    public void fromJson(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            String comment = jsonObject.getString("comment");
            UUID reporterId = UUID.fromString(jsonObject.getString("reporter_id"));
            UUID postId = UUID.fromString(jsonObject.getString("post_id"));

            this.setComment(comment);
            this.setReporterId(reporterId);
            this.setPostId(postId);
            this.setCreatedAt(new Date(System.currentTimeMillis()));;
        }catch(Exception e){
            log.info("Exception trying to convert json to ReportedPost");
            log.error(e.getMessage(),e);
        }
    }

}