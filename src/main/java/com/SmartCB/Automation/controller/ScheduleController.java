package com.SmartCB.Automation.controller;

import com.SmartCB.Automation.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/schedule")
public class ScheduleController {

    @Autowired
    private Schedule schedule;

    @GetMapping("/test")
    public ResponseEntity<String> testCronLogging() {
        schedule.logScheduledTasks();
        return ResponseEntity.ok("Logging scheduled tasks...");
    }
}
