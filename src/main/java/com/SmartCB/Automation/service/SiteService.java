package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.dto.UpdateSiteRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface SiteService {
     BaseResponse getAllSites(int page, int size);

     BaseResponse updateSite(Long id, UpdateSiteRequest request);

     BaseResponse searchSites(String searchTerm);

     BaseResponse importSitesToExcel(MultipartFile file) throws IOException;

     ByteArrayInputStream exportSitesToExcel() throws IOException;

}
