package com.subforest.controller;

import com.subforest.dto.ServiceSearchResultDto;
import com.subforest.entity.Service;
import com.subforest.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    // 예: /services/search?q=넷
    @GetMapping("/search")
    public ResponseEntity<List<ServiceSearchResultDto>> search(@RequestParam("q") String q) {
        List<Service> list = serviceRepository
                .findTop10ByNameContainingIgnoreCaseOrderByNameAsc(q);
        return ResponseEntity.ok(
                list.stream()
                        .map(s -> new ServiceSearchResultDto(s.getId(), s.getName(), s.getLogoUrl()))
                        .toList()
        );
    }
}
