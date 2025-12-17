package com.example.adminservice.repo;

import com.example.adminservice.model.UserWorkCategory;
import com.example.adminservice.model.UserWorkCategoryId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserWorkCategoryRepository extends CrudRepository<UserWorkCategory, UserWorkCategoryId> {

    @Query("""
        select uwc.id.workCategory, count(uwc)
        from UserWorkCategory uwc
        group by uwc.id.workCategory
    """)
    List<Object[]> countByCategoryRaw();
}
