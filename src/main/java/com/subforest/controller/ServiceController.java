package com.subforest.controller;

import com.subforest.dto.ServiceSearchResultDto;
import com.subforest.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
