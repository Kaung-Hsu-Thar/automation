package com.SmartCB.Automation;

import com.SmartCB.Automation.entity.SiteInfo;
import com.SmartCB.Automation.repository.SiteInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class Schedule{

    @Autowired
    private SiteInfoRepository siteInfoRepository;

    @Scheduled(fixedRate = 60000)
    public void logScheduledTasks() {
        List<SiteInfo> siteInfos = siteInfoRepository.findAll();

        for (SiteInfo siteInfo : siteInfos) {
            if ("Running".equalsIgnoreCase(siteInfo.getStatus())) {
                String onCronExpression = siteInfo.getOnSchedule();
                String offCronExpression = siteInfo.getOffSchedule();

                // Check "ON" cron schedule
                if (isCronMatch(onCronExpression)) {
                    performOnOperation(siteInfo);
                }

                // Check "OFF" cron schedule
                if (isCronMatch(offCronExpression)) {
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
                return false; // No schedule set, skip this check
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

    // Placeholder for "On" operation
    private void performOnOperation(SiteInfo siteInfo) {
        log.info("Performing ON operation for SiteCode: {} at {}",
                siteInfo.getSiteCode(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    // Placeholder for "Off" operation
    private void performOffOperation(SiteInfo siteInfo) {
        log.info("Performing OFF operation for SiteCode: {} at {}",
                siteInfo.getSiteCode(),
                LocalDateTime.now().plusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}

