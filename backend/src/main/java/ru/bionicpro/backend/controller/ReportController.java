package ru.bionicpro.backend.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bionicpro.backend.controller.dto.ReportResponse;
import ru.bionicpro.backend.service.ReportService;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

  @Autowired
  private ReportService reportService;

  @GetMapping
  public ResponseEntity<List<ReportResponse>> getUserReports(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

    String preferredUsername = jwt.getClaim("preferred_username");

    System.out.println("Generating report for user: " + preferredUsername);

    LocalDate maxAvailableDate = reportService.getMaxAvailableDate(preferredUsername);

    if (maxAvailableDate == null) {
      return ResponseEntity.noContent().build();
    }

    if (fromDate == null) {
      fromDate = LocalDate.now().minusDays(30);
    }
    if (toDate == null) {
      toDate = LocalDate.now();
    }

    List<ReportResponse> reports = reportService.getUserReports(preferredUsername, fromDate, toDate);

    if (reports.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(reports);
  }
}