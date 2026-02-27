package com.subforest.repository;

import com.subforest.dto.SubscriptionListRow;
import com.subforest.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    /**
     * [N+1 해결] fetch join을 사용하여 연관된 User와 Service 정보를 한 번에 가져옵니다.
     * 대시보드 요약 및 일반 목록 조회 시 사용됩니다.
     */
    @Query("select s from Subscription s " +
            "join fetch s.user " +
            "left join fetch s.service " +
            "left join fetch s.customService " +
            "where s.user.id = :userId")
    Page<Subscription> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * [추가] 스케줄러(ReminderScheduler)에서 사용할 전체 조회 최적화
     * 모든 구독 정보를 가져올 때 연관된 엔티티를 fetch join하여 N+1 문제를 방지합니다.
     */
    @Query("select s from Subscription s " +
            "join fetch s.user " +
            "left join fetch s.service " +
            "left join fetch s.customService")
    List<Subscription> findAllWithDetails();

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
    //Page<Subscription> findByUserId(Long userId, Pageable pageable);

}
