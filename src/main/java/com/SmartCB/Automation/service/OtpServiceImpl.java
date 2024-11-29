package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.entity.OtpInfo;
import com.SmartCB.Automation.repository.OtpInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpInfoRepository otpInfoRepository;

    private static final int OTP_EXPIRATION_MINUTES = 2;

    private void sendOtpToUser(String vmyCode, String otp) {
        // Logic to send the OTP (e.g., email or SMS)
        System.out.println("Sending OTP: " + otp + " to VMYCode: " + vmyCode);
    }

    // Generate a random 5-digit OTP
    private String generateRandomOtp() {
        Random random = new Random();
        return String.format("%05d", random.nextInt(100000));
    }

    // Generate OTP and save to database
    public String generateOtp(String vmyCode) {
        String otp = generateRandomOtp();
        OtpInfo otpInfo = new OtpInfo();
        otpInfo.setVmyCode(vmyCode);
        otpInfo.setOtp(otp);
        otpInfo.setExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        otpInfo.setRequestTime(LocalDateTime.now());
        otpInfo.setUsed(false);

        otpInfoRepository.save(otpInfo);
        // Send OTP to user
        sendOtpToUser(vmyCode, otp);
        return otp;
    }

    // Validate OTP with correct VMYcode
    public BaseResponse validateOtp(String otp, String  vmyCode) {
        Optional<OtpInfo> otpInfo = otpInfoRepository.findByOtpAndVmyCode(otp, vmyCode);

        if (otpInfo.isPresent()) {
            OtpInfo storedOtp = otpInfo.get();
            if (!storedOtp.isUsed() && storedOtp.getExpirationTime().isAfter(LocalDateTime.now())) {
                storedOtp.setUsed(true);
                otpInfoRepository.save(storedOtp);
                return new BaseResponse("000", "OTP validated successfully", null);
            } else {
                return new BaseResponse("004", "Wrong or expired OTP", null);
            }
        }
        return new BaseResponse("004", "Wrong or expired OTP", null);
    }

    // Resend OTP: Regenerate new OTP
    public BaseResponse resendOtp(String vmyCode) {
        // Always generate and send a new OTP
        String newOtp = generateOtp(vmyCode);
        return new BaseResponse("000", "New OTP generated and sent", newOtp);
    }

}

