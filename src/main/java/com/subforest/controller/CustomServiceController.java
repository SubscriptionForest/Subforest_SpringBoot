package com.subforest.controller;

import com.subforest.dto.CustomServiceCreateRequest;
import com.subforest.entity.CustomService;
import com.subforest.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/custom-services")
public class CustomServiceController {

    private final ServiceCatalogService catalog;

    @PostMapping
    public ResponseEntity<CustomService> create(@Valid @RequestBody CustomServiceCreateRequest req) {
        return ResponseEntity.ok(catalog.createCustom(req));
    }

    @GetMapping
    public ResponseEntity<List<CustomService>> list(@RequestParam Long userId) {
        return ResponseEntity.ok(catalog.listCustom(userId));
    }
}
