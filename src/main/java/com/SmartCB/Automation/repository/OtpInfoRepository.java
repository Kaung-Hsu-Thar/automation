package com.SmartCB.Automation.repository;

import com.SmartCB.Automation.entity.OtpInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpInfoRepository extends JpaRepository<OtpInfo, Long> {
    List<OtpInfo> findByVmyCode(String vmyCode);

    Optional<OtpInfo> findByOtp(String otp);

    Optional<OtpInfo> findByOtpAndVmyCode(String otp, String vmyCode);

    // Find OTP requests for a specific VMY code within the last minute
    List<OtpInfo> findByVmyCodeAndRequestTimeAfter(String vmyCode, LocalDateTime time);
}
