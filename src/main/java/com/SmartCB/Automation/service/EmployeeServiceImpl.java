package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.AuthRequest;
import com.SmartCB.Automation.dto.AuthVerify;
import com.SmartCB.Automation.entity.OtpInfo;
import com.SmartCB.Automation.repository.OtpInfoRepository;
import com.SmartCB.Automation.util.JwtUtil;
import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.entity.Employee;
import com.SmartCB.Automation.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpInfoRepository otpInfoRepository;


    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    public BaseResponse validateVMYCodeAndSendOTP(AuthRequest request) {
        // Normalize vmyCode to lowercase
        String normalizedVmyCode = request.getVmyCode().toLowerCase();

        Optional<Employee> employee = employeeRepository.findByVmyCode(normalizedVmyCode);
        if (employee.isPresent()) {
            // Check OTP request limit
            if (hasExceededRequestLimit(normalizedVmyCode)) {
                return new BaseResponse("002", "You have reached the OTP request limit. Try again later.", null);
            }
            String otp = otpService.generateOtp(normalizedVmyCode);
            return new BaseResponse("000", "We have sent OTP to your phone number", otp);
        } else {
            return new BaseResponse("001", "Your VMY doesn't exist", null);
        }
    }

    // Resend OTP
    public BaseResponse resendOTP(AuthRequest request) {
        return validateVMYCodeAndSendOTP(request);
    }

    // Updated validateOTP method to include JWT generation after OTP validation
    public BaseResponse validateOTP(AuthVerify verify) {
        // Normalize vmyCode to lowercase
        String normalizedVmyCode = verify.getVmyCode().toLowerCase();

        // Validate OTP using normalized vmyCode
        BaseResponse otpResponse = otpService.validateOtp(verify.getOtp(), normalizedVmyCode);

        // If OTP is valid, generate the JWT and return it
        if ("000".equals(otpResponse.getErrorCode())) {
            String jwtToken = jwtUtil.generateToken(normalizedVmyCode);

            // Return the JWT token in the response
            return new BaseResponse("000", "OTP validated successfully", jwtToken);
        } else {
            return otpResponse;
        }
    }


    private boolean hasExceededRequestLimit(String vmyCode) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<OtpInfo> recentRequests = otpInfoRepository.findByVmyCodeAndRequestTimeAfter(vmyCode, oneMinuteAgo);

        return recentRequests.size() >= MAX_REQUESTS_PER_MINUTE;
    }
}
