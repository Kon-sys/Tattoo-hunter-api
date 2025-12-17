package com.example.adminservice.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForecastService {

    public List<Long> linearTrendForecast(List<Long> history, int horizon) {
        int n = history.size();
        if (n == 0 || horizon <= 0) return List.of();

        if (n < 3) {
            long last = history.get(n - 1);
            List<Long> out = new ArrayList<>();
            for (int i = 0; i < horizon; i++) out.add(Math.max(0, last));
            return out;
        }

        double sumX = 0, sumY = 0, sumXX = 0, sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = history.get(i);
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXY += x * y;
        }

        double denom = (n * sumXX - sumX * sumX);
        double b = denom == 0 ? 0 : (n * sumXY - sumX * sumY) / denom;
        double a = (sumY - b * sumX) / n;

        List<Long> out = new ArrayList<>();
        for (int k = 0; k < horizon; k++) {
            double x = n + k;
            long y = Math.round(a + b * x);
            out.add(Math.max(0, y));
        }
        return out;
    }
}
