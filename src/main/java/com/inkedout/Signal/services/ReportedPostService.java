package com.inkedout.Signal.services;

import com.inkedout.Signal.domain.ReportedPost;
import com.inkedout.Signal.repositories.ReportingPostRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service

@Slf4j
public class ReportedPostService {
    private final ReportingPostRepo reportingPost;

    public ReportedPostService(ReportingPostRepo reportingPost) {
        this.reportingPost = reportingPost;
    }

    public void saveReportedPost(@org.jetbrains.annotations.NotNull ReportedPost reportedPost) {
        reportedPost.setCreatedAt(Date.from(Instant.now()));
        reportingPost.save(reportedPost);
    }
}
