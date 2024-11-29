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
import java.time.ZoneId;
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
            String cronExpression = siteInfo.getSchedule();
            if (isCronMatch(cronExpression)) {
                // Pass both siteInfo and cronExpression
                logScheduledTime(siteInfo, cronExpression);
            }
        }
    }


    private boolean isCronMatch(String cronExpression) {
        try {
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

    private void logScheduledTime(SiteInfo siteInfo, String cronExpression) {
        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Calculate the next valid time from the cron expression
        Date nextValidTime = getNextValidTime(cronExpression);

        // Log the scheduled time and cron expression
        log.info("Scheduled task triggered for SiteCode: {} at {}. Cron Expression: {}. Next valid time: {}",
                siteInfo.getSiteCode(),
                currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                cronExpression,
                nextValidTime != null ? nextValidTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A");
    }

    private Date getNextValidTime(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date());
        } catch (ParseException e) {
            log.error("Error parsing cron expression: {}", cronExpression);
            return null;
        }
    }
}

