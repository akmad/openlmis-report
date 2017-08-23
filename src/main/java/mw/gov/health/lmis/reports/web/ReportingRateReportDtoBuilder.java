package mw.gov.health.lmis.reports.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mw.gov.health.lmis.reports.dto.ReportingRateReportDto;
import mw.gov.health.lmis.reports.dto.external.BasicRequisitionDto;
import mw.gov.health.lmis.reports.dto.external.FacilityDto;
import mw.gov.health.lmis.reports.dto.external.GeographicZoneDto;
import mw.gov.health.lmis.reports.dto.external.ProcessingPeriodDto;
import mw.gov.health.lmis.reports.dto.external.ProgramDto;
import mw.gov.health.lmis.reports.dto.external.RequisitionCompletionDto;
import mw.gov.health.lmis.reports.dto.external.RequisitionStatusDto;
import mw.gov.health.lmis.reports.dto.external.StatusLogEntryDto;
import mw.gov.health.lmis.reports.service.referencedata.FacilityReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.GeographicZoneReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.PeriodReferenceDataService;
import mw.gov.health.lmis.reports.service.requisition.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportingRateReportDtoBuilder {
  private static int LATEST_PERIODS = 3;
  private static int GEOGRAPHIC_ZONE_LEVEL = 3;
  private static RequisitionStatusDto REQUIRED_STATUS = RequisitionStatusDto.APPROVED;

  @Autowired
  private PeriodReferenceDataService periodReferenceDataService;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Autowired
  private RequisitionService requisitionService;

  /**
   * Creates a DTO for reporting rate report based on given parameters.
   *
   * @param program program for reporting
   * @param period processing period for reporting
   * @param zone geographic zone for reporting
   * @return newly created report dto
   */
  public ReportingRateReportDto build(
          ProgramDto program, ProcessingPeriodDto period, GeographicZoneDto zone, Integer dueDays) {
    ReportingRateReportDto report = new ReportingRateReportDto();

    Collection<ProcessingPeriodDto> periods = getLatestPeriods(period, LATEST_PERIODS);
    Collection<GeographicZoneDto> zones = getAvailableGeographicZones(zone);
    Collection<FacilityDto> facilities = getAvailableFacilities(zones);

    report.setCompletionByPeriod(getCompletionsByPeriod(program, periods, facilities, dueDays));
    report.setCompletionByZone(getCompletionsByZone(program, periods, zones, dueDays));
    report.setPeriodName(period.getName());
    report.setProgramName(program.getName());
    if (zone != null) {
      report.setGeographicZoneName(zone.getName());
    }

    return report;
  }

  private List<RequisitionCompletionDto> getCompletionsByPeriod(
      ProgramDto program, Collection<ProcessingPeriodDto> periods,
      Collection<FacilityDto> facilities, Integer dueDays) {
    List<RequisitionCompletionDto> completionByPeriod = new ArrayList<>();

    for (ProcessingPeriodDto period : periods) {
      RequisitionCompletionDto completion = getCompletionForFacilities(
          program, Collections.singletonList(period), facilities, dueDays);
      completion.setGrouping(period.getName());
      completionByPeriod.add(completion);
    }

    return completionByPeriod;
  }

  private List<RequisitionCompletionDto> getCompletionsByZone(
      ProgramDto program, Collection<ProcessingPeriodDto> periods,
      Collection<GeographicZoneDto> zones, Integer dueDays) {
    List<RequisitionCompletionDto> completionByZone = new ArrayList<>();

    for (GeographicZoneDto zone : zones) {
      Collection<FacilityDto> facilities = getAvailableFacilities(Collections.singletonList(zone));

      if (!facilities.isEmpty()) {
        RequisitionCompletionDto completion =
            getCompletionForFacilities(program, periods, facilities, dueDays);
        completion.setGrouping(zone.getName());
        completionByZone.add(completion);
      }
    }

    // Sort by zone names
    return completionByZone
        .stream()
        .sorted((left, right) -> left.getGrouping().compareTo(right.getGrouping()))
        .collect(Collectors.toList());
  }

  private RequisitionCompletionDto getCompletionForFacilities(
      ProgramDto program, Collection<ProcessingPeriodDto> periods,
      Collection<FacilityDto> facilities, Integer dueDays) {
    CompletionCounter completions = new CompletionCounter();

    for (ProcessingPeriodDto period : periods) {
      LocalDate dueDate = period.getEndDate().plusDays(dueDays);

      for (FacilityDto facility : facilities) {
        Page<BasicRequisitionDto> requisitions = requisitionService
            .search(facility.getId(), program.getId(), null, null,
                    period.getId(), null, null, false);

        updateCompletionsWithRequisitions(completions, requisitions.getContent(), dueDate);
      }
    }

    int onTime = completions.getOnTime();
    int missed = completions.getMissed();
    int late = completions.getLate();
    int total = onTime + late + missed;

    if (total == 0) {
      missed = 1;
      total = 1;
    }

    RequisitionCompletionDto completion = new RequisitionCompletionDto();
    completion.setCompleted((onTime + late));
    completion.setMissed(missed);
    completion.setOnTime(onTime);
    completion.setLate(late);
    completion.setTotal(total);

    return completion;
  }

  void updateCompletionsWithRequisitions(
      CompletionCounter completions, List<BasicRequisitionDto> requisitions, LocalDate dueDate) {
    int missed = completions.getMissed();
    int late = completions.getLate();
    int onTime = completions.getOnTime();

    if (!requisitions.isEmpty()) {
      for (BasicRequisitionDto requisition : requisitions) {
        if (requisition.getStatusChanges() == null) {

          continue;
        }
        StatusLogEntryDto entry = requisition.getStatusChanges().getOrDefault(
            REQUIRED_STATUS.name(), null);
        if (entry == null) {
          missed++;
        } else {
          LocalDate submissionDate = entry.getChangeDate().toLocalDate();
          if (submissionDate.isAfter(dueDate)) {
            late++;
          } else {
            onTime++;
          }
        }
      }
    } else {
      missed++;
    }
    completions.setMissed(missed);
    completions.setOnTime(onTime);
    completions.setLate(late);
  }

  Collection<FacilityDto> getAvailableFacilities(Collection<GeographicZoneDto> zones) {
    List<FacilityDto> facilities = new ArrayList<>();
    for (GeographicZoneDto zone : zones) {
      facilities.addAll(facilityReferenceDataService.search(
              null, null, zone.getId(), true).getContent());
    }

    return facilities
        .stream()
        .filter(FacilityDto::getActive)
        .collect(Collectors.toList());
  }

  Collection<ProcessingPeriodDto> getLatestPeriods(ProcessingPeriodDto latest, int amount) {
    List<ProcessingPeriodDto> periods = new ArrayList<>();

    if (amount > 1) {
      // Retrieve list of periods prior to latest one
      List<ProcessingPeriodDto> previousPeriods =
          periodReferenceDataService.search(latest.getProcessingSchedule().getId(), null)
              .stream()
              .filter(p -> p.getStartDate().isBefore(latest.getStartDate()))
              // Sort by date descending -> get 2 most recent
              .sorted((left, right) -> right.getStartDate().compareTo(left.getStartDate()))
              .limit(amount - 1)
              // Sort by date ascending -> return as normal
              .sorted((left, right) -> left.getStartDate().compareTo(right.getStartDate()))
              .collect(Collectors.toList());

      periods.addAll(previousPeriods);
    }

    periods.add(latest);
    return periods;
  }

  Collection<GeographicZoneDto> getAvailableGeographicZones(GeographicZoneDto zone) {
    if (zone == null) {
      return geographicZoneReferenceDataService.search(GEOGRAPHIC_ZONE_LEVEL, null);
    } else {
      return Collections.singleton(zone);
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  class CompletionCounter {
    int missed;
    int late;
    int onTime;
  }
}
