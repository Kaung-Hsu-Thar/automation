package com.SmartCB.Automation;

import com.SmartCB.Automation.entity.CronHistory;
import com.SmartCB.Automation.entity.SiteInfo;
import com.SmartCB.Automation.repository.CronHistoryRepository;
import com.SmartCB.Automation.repository.SiteInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class Schedule{

    @Autowired
    private SiteInfoRepository siteInfoRepository;

    @Autowired
    private CronHistoryRepository cronHistoryRepository;

    @Scheduled(fixedRate = 60000)
    public void logScheduledTasks() {
        List<SiteInfo> siteInfos = siteInfoRepository.findAll();

        for (SiteInfo siteInfo : siteInfos) {
            if ("Running".equalsIgnoreCase(siteInfo.getStatus())) {
                String onCronExpression = siteInfo.getOnSchedule();
                String offCronExpression = siteInfo.getOffSchedule();

                if (isCronMatch(onCronExpression) && !isOperationAlreadyLogged(siteInfo.getSiteCode(), "ON")) {
                    performOnOperation(siteInfo);
                }

                if (isCronMatch(offCronExpression) && !isOperationAlreadyLogged(siteInfo.getSiteCode(), "OFF")) {
                    performOffOperation(siteInfo);
                }
            } else {
                log.info("Site {} is stopped. Skipping...", siteInfo.getSiteCode());
            }
        }
    }

    private boolean isCronMatch(String cronExpression) {
        try {
            if (cronExpression == null || cronExpression.isEmpty()) {
                return false;
            }

            CronExpression cron = new CronExpression(cronExpression);
            Date now = new Date();
            log.info("Current time: {}", now.toString());

            boolean isMatch = cron.isSatisfiedBy(now);
            log.info("Cron match for {}: {}", cronExpression, isMatch);
            return isMatch;
        } catch (ParseException e) {
            log.error("Invalid cron expression: {}", cronExpression);
            return false;
        }
    }

    private boolean isOperationAlreadyLogged(String siteCode, String operationType) {
        return !cronHistoryRepository.findBySiteCodeAndOperationTypeAndExecutedAtGreaterThanEqual(
                siteCode, operationType, LocalDate.now().atStartOfDay()).isEmpty();
    }

    private void performOnOperation(SiteInfo siteInfo) {
        log.info("Performing ON operation for SiteCode: {}", siteInfo.getSiteCode());

        // Log to history
        CronHistory history = new CronHistory();
        history.setSiteCode(siteInfo.getSiteCode());
        history.setOperationType("ON");
        history.setExecutedAt(LocalDateTime.now());
        cronHistoryRepository.save(history);
    }

    private void performOffOperation(SiteInfo siteInfo) {
        log.info("Performing OFF operation for SiteCode: {}", siteInfo.getSiteCode());

        // Log to history
        CronHistory history = new CronHistory();
        history.setSiteCode(siteInfo.getSiteCode());
        history.setOperationType("OFF");
        history.setExecutedAt(LocalDateTime.now());
        cronHistoryRepository.save(history);
    }
}

