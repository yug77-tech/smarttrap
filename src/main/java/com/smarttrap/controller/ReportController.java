package com.smarttrap.controller;

import com.smarttrap.service.ReportGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportGeneratorService reportGeneratorService;

    public ReportController(ReportGeneratorService reportGeneratorService) {
        this.reportGeneratorService = reportGeneratorService;
    }

    /**
     * Download full threat intelligence report as a self-contained HTML file.
     * Open in browser → File → Print → Save as PDF for a professional PDF report.
     */
    @GetMapping(value = "/download", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> downloadReport() {
        String html     = reportGeneratorService.generateHtmlReport();
        String filename = "SmartTrap-AI-Report-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm")) + ".html";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(html);
    }

    /**
     * View report inline in browser (no download prompt).
     */
    @GetMapping(value = "/view", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> viewReport() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(reportGeneratorService.generateHtmlReport());
    }
}
