package mw.gov.health.lmis.reports.service;

import static java.io.File.createTempFile;
import static mw.gov.health.lmis.reports.dto.external.TimelinessReportFacilityDto.DISTRICT_LEVEL;
import static mw.gov.health.lmis.reports.i18n.JasperMessageKeys.ERROR_JASPER_FILE_CREATION;
import static mw.gov.health.lmis.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_CLASS_NOT_FOUND;
import static mw.gov.health.lmis.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;
import static mw.gov.health.lmis.reports.i18n.ReportingMessageKeys.ERROR_REPORTING_TEMPLATE_PARAMETER_INVALID;
import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import mw.gov.health.lmis.reports.dto.ReportingRateReportDto;
import mw.gov.health.lmis.reports.dto.external.FacilityDto;
import mw.gov.health.lmis.reports.dto.external.GeographicZoneDto;
import mw.gov.health.lmis.reports.dto.external.ProcessingPeriodDto;
import mw.gov.health.lmis.reports.dto.external.ProgramDto;
import mw.gov.health.lmis.reports.dto.external.RequisitionDto;
import mw.gov.health.lmis.reports.dto.external.RequisitionStatusDto;
import mw.gov.health.lmis.reports.dto.external.TimelinessReportFacilityDto;
import mw.gov.health.lmis.reports.service.referencedata.FacilityReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.GeographicZoneReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.PeriodReferenceDataService;
import mw.gov.health.lmis.reports.service.referencedata.ProgramReferenceDataService;
import mw.gov.health.lmis.reports.service.requisition.RequisitionService;
import mw.gov.health.lmis.reports.web.ReportingRateReportDtoBuilder;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import mw.gov.health.lmis.reports.domain.JasperTemplate;
import mw.gov.health.lmis.reports.exception.JasperReportViewException;
import mw.gov.health.lmis.reports.exception.ValidationMessageException;
import mw.gov.health.lmis.utils.Message;

@Service
public class JasperReportsViewService {

  @Autowired
  private DataSource replicationDataSource;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  @Autowired
  private PeriodReferenceDataService periodReferenceDataService;

  @Autowired
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private ReportingRateReportDtoBuilder reportingRateReportDtoBuilder;

  /**
   * Create Jasper Report View.
   * Create Jasper Report (".jasper" file) from bytes from Template entity.
   * Set 'Jasper' exporter parameters, JDBC data source, web application context, url to file.
   *
   * @param jasperTemplate template that will be used to create a view
   * @param request  it is used to take web application context
   * @return created jasper view.
   * @throws JasperReportViewException if there will be any problem with creating the view.
   */
  public JasperReportsMultiFormatView getJasperReportsView(
      JasperTemplate jasperTemplate, HttpServletRequest request) throws JasperReportViewException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();
    setExportParams(jasperView);
    jasperView.setUrl(getReportUrlForReportData(jasperTemplate));
    jasperView.setJdbcDataSource(replicationDataSource);

    if (getApplicationContext(request) != null) {
      jasperView.setApplicationContext(getApplicationContext(request));
    }
    return jasperView;
  }

  /**
   * Set export parameters in jasper view.
   */
  private void setExportParams(JasperReportsMultiFormatView jasperView) {
    Map<JRExporterParameter, Object> reportFormatMap = new HashMap<>();
    reportFormatMap.put(IS_USING_IMAGES_TO_ALIGN, false);
    jasperView.setExporterParameters(reportFormatMap);
  }

  /**
   * Get application context from servlet.
   */
  public WebApplicationContext getApplicationContext(HttpServletRequest servletRequest) {
    ServletContext servletContext = servletRequest.getSession().getServletContext();
    return WebApplicationContextUtils.getWebApplicationContext(servletContext);
  }

  /**
   * Create ".jasper" file with byte array from Template.
   *
   * @return Url to ".jasper" file.
   */
  private String getReportUrlForReportData(JasperTemplate jasperTemplate)
      throws JasperReportViewException {
    File tmpFile;

    try {
      tmpFile = createTempFile(jasperTemplate.getName() + "_temp", ".jasper");
    } catch (IOException exp) {
      throw new JasperReportViewException(
          exp, ERROR_JASPER_FILE_CREATION
      );
    }

    try (ObjectInputStream inputStream =
             new ObjectInputStream(new ByteArrayInputStream(jasperTemplate.getData()))) {
      JasperReport jasperReport = (JasperReport) inputStream.readObject();

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutputStream out = new ObjectOutputStream(bos)) {

        out.writeObject(jasperReport);
        writeByteArrayToFile(tmpFile, bos.toByteArray());

        return tmpFile.toURI().toURL().toString();
      }
    } catch (IOException exp) {
      throw new JasperReportViewException(exp, ERROR_REPORTING_IO, exp.getMessage());
    } catch (ClassNotFoundException exp) {
      throw new JasperReportViewException(
          exp, ERROR_REPORTING_CLASS_NOT_FOUND, JasperReport.class.getName());
    }
  }

  private Object processParameter(Map<String, Object> params, String key, boolean required,
                                  Class paramType) {
    Message errorMessage = new Message(ERROR_REPORTING_TEMPLATE_PARAMETER_INVALID, key);

    try {
      if (!params.containsKey(key)) {
        if (required) {
          throw new ValidationMessageException(errorMessage);
        } else {
          return null;
        }
      }
      String paramValue = (String) params.get(key);

      if (UUID.class.equals(paramType)) {
        return UUID.fromString(paramValue);
      } else if (Integer.class.equals(paramType)) {
        return Integer.valueOf(paramValue);
      }
      return paramValue;
    } catch (ClassCastException | IllegalArgumentException err) {
      throw new ValidationMessageException(errorMessage, err);
    }
  }

  /**
   * Get customized Jasper Report View for Reporting Rate Report.
   *
   * @param jasperTemplate jasper template for report
   * @param request http request for filling application context
   * @param params template parameters populated with values from the request
   * @return customized jasper view.
   */
  public JasperReportsMultiFormatView getReportingRateJasperReportsView(
          JasperTemplate jasperTemplate, HttpServletRequest request, Map<String, Object> params)
          throws JasperReportViewException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();
    setExportParams(jasperView);
    jasperView.setUrl(getReportUrlForReportData(jasperTemplate));

    UUID programId = (UUID) processParameter(params, "Program", true, UUID.class);
    ProgramDto program = programReferenceDataService.findOne(programId);

    UUID periodId = (UUID) processParameter(params, "Period", true, UUID.class);
    ProcessingPeriodDto period = periodReferenceDataService.findOne(periodId);

    UUID zoneId = (UUID) processParameter(params, "GeographicZone", false, UUID.class);
    GeographicZoneDto zone = null;
    if (zoneId != null) {
      zone = geographicZoneReferenceDataService.findOne(zoneId);
    }

    Integer dueDays = (Integer) processParameter(params, "DueDays", false, Integer.class);
    if (dueDays != null && dueDays < 0) {
      throw new ValidationMessageException(
              new Message(ERROR_REPORTING_TEMPLATE_PARAMETER_INVALID, "DueDays"));
    }

    ReportingRateReportDto reportDto = reportingRateReportDtoBuilder.build(program, period, zone,
            dueDays);
    params.put("datasource", new JRBeanCollectionDataSource(Collections.singletonList(reportDto)));

    if (getApplicationContext(request) != null) {
      jasperView.setApplicationContext(getApplicationContext(request));
    }
    return jasperView;
  }

  /**
   * Get customized Jasper Report View for Timeliness Report.
   *
   * @param jasperView generic jasper report view
   * @param parameters template parameters populated with values from the request
   * @return customized jasper view.
   */
  public ModelAndView getTimelinessJasperReportView(JasperReportsMultiFormatView jasperView,
                                                    Map<String, Object> parameters) {
    ProgramDto program = programReferenceDataService.findOne(
            UUID.fromString(parameters.get("program").toString())
    );
    ProcessingPeriodDto period = periodReferenceDataService.findOne(
            UUID.fromString(parameters.get("period").toString())
    );
    GeographicZoneDto district = null;
    Object districtId = parameters.get("district");
    if (districtId != null && !districtId.toString().isEmpty()) {
      district = geographicZoneReferenceDataService.findOne(
              UUID.fromString(districtId.toString()));
    }
    List<FacilityDto> facilities = getFacilitiesForTimelinessReport(program, period, district);

    parameters.put("datasource", new JRBeanCollectionDataSource(facilities));
    parameters.put("program", program);
    parameters.put("period", period);
    parameters.put("district", district);

    return new ModelAndView(jasperView, parameters);
  }


  private List<FacilityDto> getFacilitiesForTimelinessReport(
          ProgramDto program, ProcessingPeriodDto processingPeriod, GeographicZoneDto district) {
    Set<RequisitionStatusDto> validStatuses = Arrays.stream(RequisitionStatusDto.values())
            .filter(RequisitionStatusDto::isApproved)
            .collect(Collectors.toSet());

    List<FacilityDto> facilities;
    if (district != null) {
      facilities = facilityReferenceDataService.search(null, null, district.getId(), true)
              .getContent();
    } else {
      facilities = facilityReferenceDataService.findAll();
    }

    List<TimelinessReportFacilityDto> facilitiesMissingRnR = new ArrayList<>();
    // find active facilities that are missing R&R
    for (FacilityDto facility : facilities) {
      if (facility.getActive()) {
        Page<RequisitionDto> requisitions = requisitionService.search(
                facility.getId(), program.getId(), null, null, processingPeriod.getId(),
                null, validStatuses, null);
        if (requisitions.getTotalElements() == 0) {
          TimelinessReportFacilityDto timelinessFacility = new TimelinessReportFacilityDto();
          facility.export(timelinessFacility);
          facilitiesMissingRnR.add(timelinessFacility);
        }
      }
    }

    // sort alphabetically by district and then facility name
    Comparator<FacilityDto> comparator = Comparator.comparing(
        facility -> facility.getZoneByLevelNumber(DISTRICT_LEVEL).getName());
    comparator = comparator.thenComparing(Comparator.comparing(FacilityDto::getName));

    return facilitiesMissingRnR.stream()
            .sorted(comparator).collect(Collectors.toList());
  }
}
