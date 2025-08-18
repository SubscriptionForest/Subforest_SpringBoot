package com.subforest.controller;

import com.subforest.dto.ServiceSearchResultDto;
import com.subforest.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공통 서비스 검색 엔드포인트 제공(자동완성/검색 바 용도)
// GET/services/search?q=키워드 -> ServiceCatalogService.search(q)호출 -> [ServiceSearchResultDto] 반환
@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
public class ServiceController {

    private final ServiceCatalogService catalog;

    @GetMapping("/search")
    public ResponseEntity<List<ServiceSearchResultDto>> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(catalog.search(q));
    }
}
