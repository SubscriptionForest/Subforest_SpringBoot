package com.subforest.controller;

import com.subforest.dto.CustomServiceCreateRequest;
import com.subforest.entity.CustomService;
import com.subforest.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//사용자가 직접 등록한 서비스 CRUD 중 생성/목록 담당 MVP
@RestController
@RequiredArgsConstructor
@RequestMapping("/custom-services")
public class CustomServiceController {

    private final ServiceCatalogService catalog;

    //POST/custom-services : 커스텀 서비스 생성 CUstomServiceCreateRequest
    @PostMapping
    public ResponseEntity<CustomService> create(@Valid @RequestBody CustomServiceCreateRequest req) {
        return ResponseEntity.ok(catalog.createCustom(req));
    }
    //Get/custom-services?userId= : 해당 유저의 커스텀 서비스 목록
    //JWT 적용시 userId 파라미터 제거 -> 토큰 기반으로 전환
    @GetMapping
    public ResponseEntity<List<CustomService>> list(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(catalog.listCustom(userId));
    }

}
