package com.example.adminservice.repo;

import com.example.adminservice.dto.ResponseStatusCountDto;
import com.example.adminservice.model.ResponseApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResponseApplicationRepository extends JpaRepository<ResponseApplication, Long> {

    @Query("""
           select new com.example.adminservice.dto.ResponseStatusCountDto(
               r.status,
               count(r)
           )
           from ResponseApplication r
           group by r.status
           """)
    List<ResponseStatusCountDto> countByStatus();
}
