package com.SmartCB.Automation.repository;

import com.SmartCB.Automation.entity.SiteInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SiteInfoRepository extends JpaRepository<SiteInfo, Long> {
    Page<SiteInfo> findAll(Pageable pageable);

    SiteInfo findBySiteCode(String siteCode);

    Page<SiteInfo> findBySiteCodeOrStatusContainingIgnoreCase(String siteCode, String status, Pageable pageable);
}
