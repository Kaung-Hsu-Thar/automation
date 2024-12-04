package com.SmartCB.Automation.service;

import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.dto.UpdateSiteRequest;
import com.SmartCB.Automation.entity.SiteInfo;
import com.SmartCB.Automation.repository.SiteInfoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteInfoRepository siteInfoRepository;

    public SiteServiceImpl(SiteInfoRepository siteInfoRepository) {
        this.siteInfoRepository = siteInfoRepository;
    }

    // Validate SiteCode format
    private boolean isValidSiteCode(String siteCode) {
        String regex = "^[A-Z]{3}\\d{4}$";
        return Pattern.matches(regex, siteCode);
    }

    // Validate Schedule format (Cron expression)
    private boolean isValidSchedule(String schedule) {
        String cronRegex = "^([0-5]?\\d)\\s([0-5]?\\d)\\s([01]?\\d|2[0-3])\\s(\\?|\\*|[1-9]|[12]\\d|3[01])\\s(\\*|1[0-2]|0?[1-9])\\s(\\?|\\*|[0-7])\\s(\\*|\\d{4})$";
        return Pattern.matches(cronRegex, schedule);
    }

    // Validate Status format (STOPPED or RUNNING)
    private boolean isValidStatus(String status) {
        return "STOPPED".equalsIgnoreCase(status) || "RUNNING".equalsIgnoreCase(status);
    }

    //Fetch all sites
    public BaseResponse getAllSites() {
        List<SiteInfo> sites = siteInfoRepository.findAll();
        return new BaseResponse("000", "success", sites);
    }

    // Update Site Details
    public BaseResponse updateSite(Long id, UpdateSiteRequest request) {
        if (!isValidStatus(request.getStatus())) {
            return new BaseResponse("001", "Invalid Status format", null);
        }

        SiteInfo existingSite = siteInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with Id : " + id));
        existingSite.setOnSchedule(request.getOnSchedule());
        existingSite.setOffSchedule(request.getOffSchedule());
        existingSite.setStatus(request.getStatus());
        SiteInfo updatedSite = siteInfoRepository.save(existingSite);

        return new BaseResponse("000", "Site updated successfully.", updatedSite);
    }

    // Search sites by searchTerm (siteCode, schedule, or status)
    public BaseResponse searchSites(String searchTerm) {
        List<SiteInfo> sites = siteInfoRepository.findBySiteCodeOrStatusContainingIgnoreCase(searchTerm, searchTerm);
        if (sites.isEmpty()) {
            return new BaseResponse("001", "No sites found for search term: " + searchTerm, null);
        }
        return new BaseResponse("000", "success", sites);
    }

    // Import Excel files
    public BaseResponse importSitesToExcel(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        // To hold the log for skipped (duplicate) sites
        List<String> skippedSites = new ArrayList<>();
        List<SiteInfo> savedSites = new ArrayList<>();

        // Use Apache POI to read the Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Skip the header row and check for actual data rows
            boolean hasData = false;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                // Check if the row has any non-empty cell (indicating data)
                boolean isRowEmpty = true;
                for (Cell cell : row) {
                    if (cell.getCellType() != CellType.BLANK) {
                        isRowEmpty = false;
                        break;
                    }
                }

                if (!isRowEmpty) {
                    hasData = true;
                    break;
                }
            }

            if (!hasData) {
                throw new IllegalArgumentException("File contains no valid data rows.");
            }

            // Now proceed with reading the file and processing the rows
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String siteCode = row.getCell(0).getStringCellValue();
                String onSchedule = row.getCell(1).getStringCellValue();
                String offSchedule = row.getCell(2).getStringCellValue();
                String status = row.getCell(3).getStringCellValue();

                // Validate data
                if (!isValidSiteCode(siteCode)) {
                    throw new IllegalArgumentException("Invalid SiteCode format: " + siteCode);
                }

                if (!isValidSchedule(onSchedule)) {
                    throw new IllegalArgumentException("Invalid OnSchedule format: " + onSchedule);
                }

                if (!isValidSchedule(offSchedule)) {
                    throw new IllegalArgumentException("Invalid OffSchedule format: " + offSchedule);
                }

                if (!isValidStatus(status)) {
                    throw new IllegalArgumentException("Invalid Status format: " + status);
                }

                // Check if siteCode already exists in the database
                SiteInfo existingSite = siteInfoRepository.findBySiteCode(siteCode);
                if (existingSite != null) {
                    // Collect duplicate site codes for logging
                    skippedSites.add(siteCode);
                    continue;
                }

                // If validation passes, save the site information
                SiteInfo siteInfo = new SiteInfo();
                siteInfo.setSiteCode(siteCode);
                siteInfo.setOnSchedule(onSchedule);
                siteInfo.setOffSchedule(offSchedule);
                siteInfo.setStatus(status);

                // Save the SiteInfo object
                siteInfoRepository.save(siteInfo);
                savedSites.add(siteInfo);
            }
        }

        // Return a detailed response
        if (savedSites.isEmpty() && skippedSites.isEmpty()) {
            return new BaseResponse("001", "No valid data to import.", null);
        }

        String successMessage = savedSites.size() + " sites imported successfully.";
        String skipMessage = skippedSites.isEmpty() ? "" : skippedSites.size() + " duplicate sites skipped: " + String.join(", ", skippedSites);

        return new BaseResponse("000", successMessage + " " + skipMessage, null);
    }


    // Export site data to Excel
    public ByteArrayInputStream exportSitesToExcel() throws IOException {
        List<SiteInfo> sites = siteInfoRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sites");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Site Code", "OnSchedule", "OffSchedule", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // Populate rows
            int rowIndex = 1;
            for (SiteInfo site : sites) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(site.getSiteCode());
                row.createCell(1).setCellValue(site.getOnSchedule());
                row.createCell(2).setCellValue(site.getOffSchedule());
                row.createCell(3).setCellValue(site.getStatus());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
