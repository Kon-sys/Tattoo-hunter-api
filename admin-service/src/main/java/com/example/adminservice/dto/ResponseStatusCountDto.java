package com.example.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseStatusCountDto {
    private String status;
    private long count;
}
