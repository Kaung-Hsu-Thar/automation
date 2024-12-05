package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.AuthRequest;
import com.SmartCB.Automation.dto.AuthVerify;
import com.SmartCB.Automation.dto.BaseResponse;

public interface EmployeeService {
    BaseResponse validateVMYCodeAndSendOTP(AuthRequest request);

    BaseResponse resendOTP(AuthRequest request);

    BaseResponse validateOTP(AuthVerify verify);

}
