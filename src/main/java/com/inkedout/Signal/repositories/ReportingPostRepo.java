package com.inkedout.Signal.repositories;

import com.inkedout.Signal.domain.ReportedPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReportingPostRepo extends JpaRepository<ReportedPost, Long> {
}
