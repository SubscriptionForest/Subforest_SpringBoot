package com.subforest.repository;

import com.subforest.dto.SubscriptionListRow;
import com.subforest.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/*
 * SubscriptionRepository:
 * - findByUserId: 기본 목록 조회. 페이지네이션/정렬과 함께 사용
 * - findUpcomingOrder: 다음 결제일을 DB에서 계산하여 임박순 정렬(네이티브)
 *  (Projection DTO: SubscriptionListRow 로 결과 매핑)
 *  계산식 핵심:

        CURDATE() <= start_date면 start_date가 다음 결제일
        아니면 (지난 일수/주기)+1회차만큼 더한 날짜가 다음 결제일
        remainingDays = DATEDIFF(nextBillingDate, CURDATE())
 */


public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = """
        SELECT 
          s.id,
          COALESCE(svc.name, csv.name) AS serviceName,
          COALESCE(svc.logo_url, csv.logo_url) AS logoUrl,
          s.amount,
          s.repeat_cycle_days AS repeatCycleDays,
          CASE 
            WHEN CURDATE() <= s.start_date THEN s.start_date
            ELSE DATE_ADD(
                   s.start_date,
                   INTERVAL (FLOOR(DATEDIFF(CURDATE(), s.start_date)/s.repeat_cycle_days)+1) * s.repeat_cycle_days DAY
                 )
          END AS nextBillingDate,
          DATEDIFF(
            CASE 
              WHEN CURDATE() <= s.start_date THEN s.start_date
              ELSE DATE_ADD(
                     s.start_date,
                     INTERVAL (FLOOR(DATEDIFF(CURDATE(), s.start_date)/s.repeat_cycle_days)+1) * s.repeat_cycle_days DAY
                   )
            END,
            CURDATE()
          ) AS remainingDays,
          s.auto_payment AS autoPayment,
          s.is_shared AS isShared
        FROM subscriptions s
        LEFT JOIN services svc ON svc.id = s.service_id
        LEFT JOIN custom_services csv ON csv.id = s.custom_service_id
        WHERE s.user_id = :userId
        ORDER BY nextBillingDate ASC
        """,
            countQuery = "SELECT COUNT(*) FROM subscriptions s WHERE s.user_id = :userId",
            nativeQuery = true)
    Page<SubscriptionListRow> findUpcomingOrder(@Param("userId") Long userId, Pageable pageable);

    // 목록 조회 시 service, customService를 함께 로딩하여 N+1 제거
    @EntityGraph(attributePaths = {"service", "customService"})
    Page<Subscription> findByUserId(Long userId, Pageable pageable);

    // 상세 조회 시에도 연관 로딩이 필요할 수 있어 fetch join 버전 (선택 사용)
    @Query("""
        select s
        from Subscription s
        left join fetch s.service
        left join fetch s.customService
        left join fetch s.user
        where s.id = :id
    """)
    Optional<Subscription> findByIdWithJoins(@Param("id") Long id);
}
