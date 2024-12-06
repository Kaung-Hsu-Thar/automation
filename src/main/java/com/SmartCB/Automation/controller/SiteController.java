package com.SmartCB.Automation.controller;

import com.SmartCB.Automation.dto.BaseResponse;
import com.SmartCB.Automation.dto.UpdateSiteRequest;
import com.SmartCB.Automation.service.SiteService;
import com.SmartCB.Automation.service.SiteServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteServiceImpl siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/sites")
    public ResponseEntity<BaseResponse> getAllSites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(siteService.getAllSites(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateSite(@PathVariable Long id, @RequestBody UpdateSiteRequest request) {
        return ResponseEntity.ok(siteService.updateSite(id, request));
    }

    @GetMapping("/search")
    @Operation(summary = "Search sites", description = "Searches for sites by siteCode, schedule, or status.")
    public ResponseEntity<BaseResponse> searchSites(@RequestParam("searchTerm") String searchTerm) {
        BaseResponse response = siteService.searchSites(searchTerm);
        return ResponseEntity.ok(response);
    }

    // Import Excel file
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import Excel file", description = "Uploads an Excel file to import site data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Your file imported successfully."),
            @ApiResponse(responseCode = "400", description = "Error importing file!")
    })
    public BaseResponse importSites(@RequestParam("file") @Parameter(description = "The Excel file to upload") MultipartFile file) {
        try {
            // Use the service method which now returns a response
            return siteService.importSitesToExcel(file);
        } catch (Exception e) {
            return new BaseResponse("001", "Error processing file: " + e.getMessage(), null);
        }
    }

    // Export Excel file
    @GetMapping("/export")
    @Operation(summary = "Export Site Information", description = "Exports all site information to an Excel file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File exported successfully."),
            @ApiResponse(responseCode = "500", description = "Error exporting file!")
    })
    public ResponseEntity<InputStreamResource> exportSites() {
        try {
            ByteArrayInputStream file = siteService.exportSitesToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=site_information.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(file));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Site Template");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("SiteCode");
        headerRow.createCell(1).setCellValue("OnSchedule");
        headerRow.createCell(2).setCellValue("OffSchedule");
        headerRow.createCell(3).setCellValue("Status");

        // Write the output to a ByteArrayOutputStream
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);

            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=site_template.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // Return the Excel file as a byte array
            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
        }
    }
}
