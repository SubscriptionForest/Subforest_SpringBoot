package com.subforest.controller;

import com.subforest.dto.DashboardSummaryDto;
import com.subforest.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> summary(@RequestParam Long userId) {
        return ResponseEntity.ok(dashboardService.summary(userId));
    }
}
