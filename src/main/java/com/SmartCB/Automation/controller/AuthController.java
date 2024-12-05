package com.SmartCB.Automation.controller;

import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.repository.EmployeeRepository;
import com.SmartCB.Automation.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Login: Validate VMYCode and send OTP
    @PostMapping("/request-otp-login")
    public BaseResponse login(@RequestParam String vmyCode) {
        return employeeService.validateVMYCodeAndSendOTP(vmyCode);
    }

    // Resend OTP if needed
    @PostMapping("/resend-otp")
    public BaseResponse resendOTP(@RequestParam String vmyCode) {
        return employeeService.resendOTP(vmyCode);
    }

    // Endpoint to verify OTP
    @PostMapping("/verify-otp-login")
    public BaseResponse verifyOTP(@RequestParam String otp, @RequestParam String vmyCode) {
        return employeeService.validateOTP(otp, vmyCode);
    }
}
