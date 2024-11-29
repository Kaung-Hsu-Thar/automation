package com.SmartCB.Automation.repository;

import com.SmartCB.Automation.entity.SiteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteInfoRepository extends JpaRepository<SiteInfo, Long> {
    List<SiteInfo> findAll();

    SiteInfo findBySiteCode(String siteCode);

    List<SiteInfo> findBySiteCodeOrStatusContainingIgnoreCase(String siteCode, String status);
}
