package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.BaseResponse;

public interface EmployeeService {
    BaseResponse validateVMYCodeAndSendOTP(String vmyCode);

    BaseResponse resendOTP(String vmyCode);

    BaseResponse validateOTP(String otp, String vmyCode);

}
