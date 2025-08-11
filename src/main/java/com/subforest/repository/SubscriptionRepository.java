package com.subforest.repository;

import com.subforest.dto.SubscriptionListRow;
import com.subforest.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

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
    Page<Subscription> findByUserId(Long userId, Pageable pageable);
}
