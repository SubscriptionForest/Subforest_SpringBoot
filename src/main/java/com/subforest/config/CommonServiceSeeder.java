package com.subforest.config;

import com.subforest.entity.Service;
import com.subforest.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 앱 시작 시 공통 서비스 카탈로그를 채워넣는다.
 * 이미 존재하면 건너뜀.
 */
@Component
@RequiredArgsConstructor
public class CommonServiceSeeder implements CommandLineRunner {

    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) {
        // 서비스명 -> (카테고리, 로고 URL)  (카테고리는 현재 엔티티에 저장 안함, 참조용)
        Map<String, String> logos = Map.ofEntries(
                // OTT / 영상
                Map.entry("넷플릭스", "/static/logo/netflix.png"),
                Map.entry("웨이브", "/static/logo/wavve.png"),
                Map.entry("티빙", "/static/logo/tving.png"),
                Map.entry("디즈니플러스", "/static/logo/disneyplus.png"),
                Map.entry("왓챠", "/static/logo/watcha.png"),
                Map.entry("쿠팡플레이", "/static/logo/coupangplay.png"),

                // 음악
                Map.entry("스포티파이", "/static/logo/spotify.png"),
                Map.entry("애플뮤직", "/static/logo/applemusic.png"),
                Map.entry("멜론", "/static/logo/melon.png"),
                Map.entry("유튜브뮤직", "/static/logo/youtubemusic.png"),
                Map.entry("지니", "/static/logo/genie.png"),
                Map.entry("플로", "/static/logo/flo.png"),
                Map.entry("벅스", "/static/logo/bugs.png"),

                // 멤버십
                Map.entry("네이버 플러스 멤버십", "/static/logo/naverplus.png"),
                Map.entry("유튜브 프리미엄", "/static/logo/youtubepremium.png"),

                // 전자책
                Map.entry("밀리의서재", "/static/logo/millie.png"),

                // 생산성/소프트웨어
                Map.entry("Notion", "/static/logo/notion.png"),
                Map.entry("ChatGPT", "/static/logo/chatgpt.png"),
                Map.entry("Gemini", "/static/logo/gemini.png"),
                Map.entry("Adobe Creative Cloud", "/static/logo/adobecc.png")
        );

        for (Map.Entry<String, String> e : logos.entrySet()) {
            String name = e.getKey();
            String logo = e.getValue();

            // 이미 있으면 skip (ServiceRepository에 findByName(String) 존재 가정)
            if (serviceRepository.findByName(name).isEmpty()) {
                Service s = new Service();
                s.setName(name);
                s.setLogoUrl(logo); // 엔티티에 logoUrl 필드 있음
                serviceRepository.save(s);
            }
        }
    }
}
