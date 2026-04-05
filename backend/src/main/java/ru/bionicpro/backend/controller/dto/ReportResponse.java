package ru.bionicpro.backend.controller.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReportResponse {

  private LocalDate reportDate;
  private String userId;
  private String prosthesisId;
  private String userName;
  private String prosthesisModel;
  private Integer totalSessions;
  private Double avgSignalStrength;
  private Integer totalMovements;
  private Integer errorCount;
  private LocalDate updatedAt;
}