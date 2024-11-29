package com.SmartCB.Automation.dto;

import com.SmartCB.Automation.validation.CronRegex;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSiteRequest {
    @CronRegex
    private String schedule;

    @NotBlank
    private String status;

}
