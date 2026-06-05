package com.inkedout.Signal.repositories;

import com.inkedout.Signal.domain.ReportedCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportedCategoryRepo extends JpaRepository<com.inkedout.Signal.domain.ReportedCategory, Long> {
    Optional<ReportedCategory> findByName(String category);
}
