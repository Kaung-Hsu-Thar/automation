package com.SmartCB.Automation.repository;

import com.SmartCB.Automation.entity.CronHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CronHistoryRepository extends JpaRepository<CronHistory, Long> {
    List<CronHistory> findBySiteCodeAndOperationTypeAndExecutedAtGreaterThanEqual(String siteCode, String operationType, LocalDateTime startDate);
}
