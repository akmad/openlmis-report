package mw.gov.health.lmis.reports.web;

import guru.nidi.ramltester.junit.RamlMatchers;
import mw.gov.health.lmis.reports.dto.external.GeographicLevelDto;
import mw.gov.health.lmis.reports.dto.external.GeographicZoneDto;
import mw.gov.health.lmis.reports.dto.external.ProcessingPeriodDto;
import mw.gov.health.lmis.reports.dto.external.ProcessingScheduleDto;
import mw.gov.health.lmis.reports.dto.external.ProgramDto;
import mw.gov.health.lmis.reports.dto.external.StockAdjustmentReasonDto;
import mw.gov.health.lmis.reports.service.referencedata.GeographicZoneReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.PeriodReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.ProgramReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.StockAdjustmentReasonReferenceDataService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@SuppressWarnings("PMD.TooManyMethods")
public class ReportsControllerIntegrationTest extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/reports";
  private static final String NAME = "name";
  private static final String CODE = "code";
  private static final String DESCRIPTION = "description";

  @MockBean
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @MockBean
  private PeriodReferenceDataService periodReferenceDataService;

  @MockBean
  private ProgramReferenceDataService programReferenceDataService;

  @MockBean
  private StockAdjustmentReasonReferenceDataService stockAdjustmentReasonReferenceDataService;

  @Before
  public void setUp() {
    mockUserAuthenticated();
  }

  // GET /api/reports/districts

  @Test
  public void shouldGetAllDistricts() {
    // given
    GeographicZoneDto[] zones = { generateGeographicZone(), generateGeographicZone() };
    given(geographicZoneReferenceDataService.search(3, null)).willReturn(Arrays.asList(zones));

    // when
    GeographicZoneDto[] result = restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL + "/districts")
        .then()
        .statusCode(200)
        .extract().as(GeographicZoneDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/processingPeriods

  @Test
  public void shouldGetAllProcessingPeriods() {
    // given
    ProcessingPeriodDto[] periods = { generateProcessingPeriod(), generateProcessingPeriod() };
    given(periodReferenceDataService.findAll()).willReturn(Arrays.asList(periods));

    // when
    ProcessingPeriodDto[] result = restAssured.given()
            .queryParam(ACCESS_TOKEN, getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(RESOURCE_URL + "/processingPeriods")
            .then()
            .statusCode(200)
            .extract().as(ProcessingPeriodDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/programs

  @Test
  public void shouldGetAllPrograms() {
    // given
    ProgramDto[] programs = { generateProgram(), generateProgram() };
    given(programReferenceDataService.findAll()).willReturn(Arrays.asList(programs));

    // when
    ProgramDto[] result = restAssured.given()
            .queryParam(ACCESS_TOKEN, getToken())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(RESOURCE_URL + "/programs")
            .then()
            .statusCode(200)
            .extract().as(ProgramDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/stockAdjustmentReasons/search

  @Test
  public void shouldGetStockAdjustmentReasonsByProgram() {
    // given
    UUID programId = UUID.randomUUID();
    StockAdjustmentReasonDto[] reasons
            = { generateStockAdjustmentReason(), generateStockAdjustmentReason() };
    given(stockAdjustmentReasonReferenceDataService.search(programId))
            .willReturn(Arrays.asList(reasons));

    // when
    StockAdjustmentReasonDto[] result = restAssured.given()
            .queryParam(ACCESS_TOKEN, getToken())
            .queryParam("program", programId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(RESOURCE_URL + "/stockAdjustmentReasons/search")
            .then()
            .statusCode(200)
            .extract().as(StockAdjustmentReasonDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private GeographicZoneDto generateGeographicZone() {
    GeographicZoneDto zone = new GeographicZoneDto();
    zone.setId(UUID.randomUUID());
    zone.setCode(CODE);
    zone.setName(NAME);

    GeographicLevelDto level = new GeographicLevelDto();
    level.setId(UUID.randomUUID());
    level.setCode(CODE);
    level.setName(NAME);
    level.setLevelNumber(3);

    zone.setLevel(level);
    return zone;
  }

  private ProcessingPeriodDto generateProcessingPeriod() {
    ProcessingPeriodDto period = new ProcessingPeriodDto();
    period.setId(UUID.randomUUID());
    period.setName(NAME);
    period.setDescription(DESCRIPTION);
    period.setDurationInMonths(1);
    period.setStartDate(LocalDate.MIN);
    period.setEndDate(LocalDate.MAX);

    ProcessingScheduleDto schedule = new ProcessingScheduleDto();
    schedule.setId(UUID.randomUUID());
    schedule.setName(NAME);
    schedule.setCode(CODE);
    schedule.setDescription(DESCRIPTION);
    schedule.setModifiedDate(ZonedDateTime.now());
    period.setProcessingSchedule(schedule);

    return period;
  }

  private ProgramDto generateProgram() {
    ProgramDto program = new ProgramDto();
    program.setId(UUID.randomUUID());
    program.setCode(CODE);
    program.setName(NAME);
    program.setDescription(DESCRIPTION);
    program.setActive(true);
    program.setPeriodsSkippable(true);
    program.setShowNonFullSupplyTab(true);

    return program;
  }

  private StockAdjustmentReasonDto generateStockAdjustmentReason() {
    StockAdjustmentReasonDto reason = new StockAdjustmentReasonDto();
    reason.setId(UUID.randomUUID());
    reason.setName(NAME);
    reason.setDescription(DESCRIPTION);
    reason.setAdditive(true);
    reason.setDisplayOrder(1);
    reason.setProgram(generateProgram());

    return reason;
  }
}
