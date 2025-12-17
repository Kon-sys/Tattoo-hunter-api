package com.example.adminservice.repo;

import com.example.adminservice.dto.ResponseStatusCountDto;
import com.example.adminservice.model.ResponseApplication;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponseApplicationRepository extends JpaRepository<ResponseApplication, Long> {

    @Query("""
        select new com.example.adminservice.dto.ResponseStatusCountDto(r.status, count(r))
        from ResponseApplication r
        where (:companyId is null or r.companyId = :companyId)
        group by r.status
    """)
    List<ResponseStatusCountDto> countByStatus(@Param("companyId") Long companyId);

    @Query("""
        select r from ResponseApplication r
        where (:companyId is null or r.companyId = :companyId)
    """)
    List<ResponseApplication> findAllForAnalytics(@Param("companyId") Long companyId);
}
