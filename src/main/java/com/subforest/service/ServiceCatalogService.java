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

/**
 * 공통 서비스(넷플릭스 등) 검색과, 사용자 커스텀 서비스 등록/조회 비즈니스 로직을 담당.
 * 컨트롤러 ServiceController, CustomServiceController가 이 서비스만 호출
 */
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

    //User 조회 후 CustomService 생성/조회
    //로고 미지정시 기본 경로로 보정
    public CustomService createCustom(CustomServiceCreateRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CustomService cs = new CustomService();
        cs.setUser(user);
        cs.setName(req.getName());
        cs.setLogoUrl(req.getLogoUrl() != null ? req.getLogoUrl() : "/static/images/default_service.png");
        return customServiceRepository.save(cs);
    }
    //목록반환
    public List<CustomService> listCustom(Long userId) {
        return customServiceRepository.findByUser_Id(userId);
    }
}
