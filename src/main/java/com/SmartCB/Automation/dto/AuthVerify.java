package com.SmartCB.Automation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthVerify {
    @NotBlank
    private String vmyCode;

    @NotBlank
    private String otp;
}