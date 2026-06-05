package com.inkedout.Signal.domain;

import com.inkedout.Signal.services.ReportedCategoryService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
@Entity
@NoArgsConstructor
@Table(name = "reported_categories", schema = "public")
public class ReportedCategory {

    @Id
    @Setter
    @Getter
    @Column(name = "id", nullable = false)
    private Long id;


    @Setter
    @Getter
    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    public ReportedCategory fromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        ReportedCategory reportedCategory = new ReportedCategory();
        try{
            Long id = jsonObject.optLong("id");
            reportedCategory.setName(jsonObject.getString("name"));
            reportedCategory.setId(id);
            return reportedCategory;
        }catch(Exception e){
            log.info("Exception trying to parse reported category json");
            log.error(e.getMessage());
        }
        return null;
    }
}