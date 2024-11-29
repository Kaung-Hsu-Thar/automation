package com.SmartCB.Automation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SMARTCB_CRON_HISTORY")
public class CronHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String siteCode;

    @Column(nullable = false)
    private String operationType;

    @Column(nullable = false)
    private LocalDateTime executedAt;
}
