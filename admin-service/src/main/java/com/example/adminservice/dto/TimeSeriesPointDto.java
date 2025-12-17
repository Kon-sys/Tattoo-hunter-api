package com.example.adminservice.dto;

public record TimeSeriesPointDto(String period, long value, boolean forecast) {}
