package com.example.adminservice.dto;

import lombok.Data;

@Data
public class ResponseStatusCountDto {

    private String status;
    private long count;

    public ResponseStatusCountDto(String status, long count) {
        this.status = status;
        this.count = count;
    }
}
