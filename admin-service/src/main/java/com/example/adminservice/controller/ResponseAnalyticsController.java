package com.example.adminservice.controller;

import com.example.adminservice.dto.ResponseConversionDto;
import com.example.adminservice.service.ResponseAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics/responses")
@RequiredArgsConstructor
public class ResponseAnalyticsController {

    private final ResponseAnalyticsService service;

    @GetMapping("/conversion")
    public ResponseEntity<ResponseConversionDto> conversion(
            @RequestParam(required = false) Long companyId
    ) {
        return ResponseEntity.ok(service.getResponsesConversion(companyId));
    }
}
