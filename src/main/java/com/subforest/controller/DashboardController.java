package com.subforest.controller;

import com.subforest.dto.DashboardSummaryDto;
import com.subforest.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//홈 화면 요약 데이터를 노출하는 REST 컨트롤러.
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    //앱 최초 진입/새로고침 시 호출 → 총액, 개수, 파이/막대 데이터 수신 → 차트 렌더링
    //JWT 적용 시 userId 제거하고 인증 사용자 기준으로 조회
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> summary(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(dashboardService.summary(userId));
    }
}
