package mw.gov.health.lmis.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.reports.dto.external.RequisitionCompletionDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportingRateReportDto {
  /**
   * List of requisition completions grouped by periods.
   */
  private List<RequisitionCompletionDto> completionByPeriod;

  /**
   * List of requisition completions grouped by geographic zones.
   */
  private List<RequisitionCompletionDto> completionByZone;

  private String programName;

  private String periodName;

  private String geographicZoneName;
}
