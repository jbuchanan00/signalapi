package com.inkedout.Signal.services;

import com.inkedout.Signal.domain.ReportedCategory;
import com.inkedout.Signal.repositories.ReportedCategoryRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportedCategoryService {
    private final ReportedCategoryRepo reportedCategory;


    ReportedCategoryService(ReportedCategoryRepo reportedCategory) {
        this.reportedCategory = reportedCategory;
    }

    public ReportedCategory getReportedCategory(String category) {
        Optional<ReportedCategory> categoryId = reportedCategory.findByName(category);
        return categoryId.orElse(null);
    }
}
