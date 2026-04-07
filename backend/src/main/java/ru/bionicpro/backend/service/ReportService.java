package ru.bionicpro.backend.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.bionicpro.backend.controller.dto.ReportResponse;


@Service
public class ReportService {

  @Autowired
  private JdbcTemplate clickHouseJdbcTemplate;

  public LocalDate getMaxAvailableDate(String userId) {
    String maxDateSql = "SELECT MAX(report_date) FROM prosthesis_daily_fact WHERE user_id = ?";
    LocalDate maxAvailableDate = clickHouseJdbcTemplate.queryForObject(maxDateSql, LocalDate.class,
        userId);
    return maxAvailableDate;
  }

  public List<ReportResponse> getUserReports(String userId, LocalDate fromDate, LocalDate toDate) {
    String sql = """
            SELECT 
                report_date,
                user_id,
                prosthesis_id,
                user_name,
                prosthesis_model,
                total_sessions,
                avg_signal_strength,
                total_movements,
                error_count
            FROM prosthesis_daily_fact
            WHERE user_id = ?
              AND report_date BETWEEN ? AND ?
            ORDER BY report_date DESC
        """;

    return clickHouseJdbcTemplate.query(sql, new Object[]{userId, fromDate, toDate},
        (rs, rowNum) -> {
          ReportResponse report = new ReportResponse();
          report.setReportDate(rs.getDate("report_date").toLocalDate());
          report.setUserId(rs.getString("user_id"));
          report.setProsthesisId(rs.getString("prosthesis_id"));
          report.setUserName(rs.getString("user_name"));
          report.setProsthesisModel(rs.getString("prosthesis_model"));
          report.setTotalSessions(rs.getInt("total_sessions"));
          report.setAvgSignalStrength(rs.getDouble("avg_signal_strength"));
          report.setTotalMovements(rs.getInt("total_movements"));
          report.setErrorCount(rs.getInt("error_count"));
          return report;
        });
  }
}