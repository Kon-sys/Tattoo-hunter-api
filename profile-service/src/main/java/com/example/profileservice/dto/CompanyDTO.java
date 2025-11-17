package com.example.profileservice.dto;

import lombok.Data;


@Data
public class CompanyDTO {
    private Long id;
    private Long userId;
    private String name;
    private String city;
    private String address;
}
