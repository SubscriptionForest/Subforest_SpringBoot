package com.subforest.service;

import com.subforest.dto.CustomServiceCreateRequest;
import com.subforest.dto.ServiceSearchResultDto;
import com.subforest.entity.CustomService;
import com.subforest.entity.User;
import com.subforest.repository.CustomServiceRepository;
import com.subforest.repository.ServiceRepository;
import com.subforest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final CustomServiceRepository customServiceRepository;
    private final UserRepository userRepository;

    public List<ServiceSearchResultDto> search(String q) {
        return serviceRepository.findTop10ByNameContainingIgnoreCaseOrderByNameAsc(q)
                .stream()
                .map(s -> new ServiceSearchResultDto(s.getId(), s.getName(), s.getLogoUrl()))
                .toList();
    }

    public CustomService createCustom(CustomServiceCreateRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CustomService cs = new CustomService();
        cs.setUser(user);
        cs.setName(req.getName());
        cs.setLogoUrl(req.getLogoUrl() != null ? req.getLogoUrl() : "/static/images/default_service.png");
        return customServiceRepository.save(cs);
    }

    public List<CustomService> listCustom(Long userId) {
        return customServiceRepository.findByUser_Id(userId);
    }
}
