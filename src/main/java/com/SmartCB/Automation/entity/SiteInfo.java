package com.SmartCB.Automation.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "SMARTCB_SITE_INFO")
public class SiteInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String siteCode;
    private String onSchedule;
    private String offSchedule;

    private String status;


}
