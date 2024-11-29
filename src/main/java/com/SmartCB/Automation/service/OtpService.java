package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.BaseResponse;

public interface OtpService {
    String generateOtp(String vmyCode);

    BaseResponse validateOtp(String otp, String vmyCode);

    BaseResponse resendOtp(String vmyCode);


}
