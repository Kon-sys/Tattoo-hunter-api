package com.example.adminservice.repo;

import com.example.adminservice.model.UserWorkCategory;
import com.example.adminservice.model.UserWorkCategoryId;
import com.example.adminservice.model.WorkCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserWorkCategoryRepository extends JpaRepository<UserWorkCategory, UserWorkCategoryId> {

    @Query("""
           select uwc.workCategory as category,
                  count(distinct uwc.userId) as cnt
           from UserWorkCategory uwc
           group by uwc.workCategory
           """)
    List<Object[]> countEmployeesByCategoryRaw();
}
