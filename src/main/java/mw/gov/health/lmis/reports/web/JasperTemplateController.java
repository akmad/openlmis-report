package mw.gov.health.lmis.reports.web;

import static mw.gov.health.lmis.reports.i18n.JasperMessageKeys.ERROR_JASPER_TEMPLATE_NOT_FOUND;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import mw.gov.health.lmis.reports.domain.JasperTemplate;
import mw.gov.health.lmis.reports.dto.JasperTemplateDto;
import mw.gov.health.lmis.reports.exception.JasperReportViewException;
import mw.gov.health.lmis.reports.exception.NotFoundMessageException;
import mw.gov.health.lmis.reports.exception.ReportingException;
import mw.gov.health.lmis.reports.repository.JasperTemplateRepository;
import mw.gov.health.lmis.reports.service.JasperReportsViewService;
import mw.gov.health.lmis.reports.service.JasperTemplateService;
import mw.gov.health.lmis.reports.service.PermissionService;
import mw.gov.health.lmis.utils.Message;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@Controller
@Transactional
@RequestMapping("/api/reports/templates/malawi")
public class JasperTemplateController extends BaseController {
  private static final Logger LOGGER = Logger.getLogger(JasperTemplateController.class);

  private static final String TIMELINESS_REPORT = "Timeliness Report";
  private static final String REPORTING_RATE_REPORT = "Reporting Rate Report";
  private static final int DUE_DAYS = 10;
  private static final String CONSISTENCY_REPORT = "Consistency Report";

  @Autowired
  private JasperTemplateService jasperTemplateService;

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Autowired
  private JasperReportsViewService jasperReportsViewService;

  @Autowired
  private PermissionService permissionService;

  /**
   * Adding report templates with ".jrxml" format to database.
   *
   * @param file        File in ".jrxml" format to upload
   * @param name        Name of file in database
   * @param description Description of the file
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void createJasperReportTemplate(
      @RequestPart("file") MultipartFile file, String name, String description)
      throws ReportingException {
    permissionService.canEditReportTemplates();

    JasperTemplate jasperTemplateToUpdate = jasperTemplateRepository.findByName(name);
    if (jasperTemplateToUpdate == null) {
      LOGGER.debug("Creating new template");
      jasperTemplateToUpdate = new JasperTemplate(
          name, null, CONSISTENCY_REPORT, description, null);
      jasperTemplateService.validateFileAndInsertTemplate(jasperTemplateToUpdate, file);
    } else {
      LOGGER.debug("Template found, updating template");
      jasperTemplateToUpdate.setDescription(description);
      jasperTemplateService.validateFileAndSaveTemplate(jasperTemplateToUpdate, file);
    }

    LOGGER.debug("Saved template with id: " + jasperTemplateToUpdate.getId());
  }

  /**
   * Get all templates.
   *
   * @return Templates.
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<JasperTemplateDto> getAllTemplates() {
    permissionService.canViewReports();
    return JasperTemplateDto.newInstance(jasperTemplateRepository.findAll())
        .stream()
        // filter out the Aggregate Orders Report
        .filter(template -> !template.getId().equals(
            UUID.fromString("f28d0ebd-7276-4453-bc3c-48556a4bd25a")))
        .collect(Collectors.toList());
  }

  /**
   * Get chosen template.
   *
   * @param templateId UUID of template which we want to get
   * @return Template.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public JasperTemplateDto getTemplate(@PathVariable("id") UUID templateId) {
    permissionService.canViewReports();
    JasperTemplate jasperTemplate =
        jasperTemplateRepository.findOne(templateId);
    if (jasperTemplate == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    }

    return JasperTemplateDto.newInstance(jasperTemplate);
  }

  /**
   * Allows deleting template.
   *
   * @param templateId UUID of template which we want to delete
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTemplate(@PathVariable("id") UUID templateId) {
    permissionService.canEditReportTemplates();
    JasperTemplate jasperTemplate = jasperTemplateRepository.findOne(templateId);
    if (jasperTemplate == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    } else {
      jasperTemplateRepository.delete(jasperTemplate);
    }
  }

  /**
   * Generate a report based on the template, the format and the request parameters.
   *
   * @param request    request (to get the request parameters)
   * @param templateId report template ID
   * @param format     report format to generate, default is PDF
   * @return the generated report
   */
  @RequestMapping(value = "/{id}/{format}", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView generateReport(HttpServletRequest request,
                                     @PathVariable("id") UUID templateId,
                                     @PathVariable("format") String format)
      throws JasperReportViewException {

    permissionService.canViewReports();

    JasperTemplate template = jasperTemplateRepository.findOne(templateId);

    if (template == null) {
      throw new NotFoundMessageException(new Message(
          ERROR_JASPER_TEMPLATE_NOT_FOUND, templateId));
    }

    Map<String, Object> map = jasperTemplateService.mapRequestParametersToTemplate(
        request, template
    );
    map.put("format", format);
    map.put("imagesDirectory", "images/");

    JasperReportsMultiFormatView jasperView;

    if (REPORTING_RATE_REPORT.equals(template.getType())) {
      map.putIfAbsent("DueDays", String.valueOf(DUE_DAYS));

      jasperView = jasperReportsViewService.getReportingRateJasperReportsView(
          template, request, map
      );
    } else {
      jasperView = jasperReportsViewService.getJasperReportsView(template, request);
    }

    String fileName = template.getName().replaceAll("\\s+", "_");
    String contentDisposition = "inline; filename=" + fileName + "." + format;

    jasperView
        .getContentDispositionMappings()
        .setProperty(format, contentDisposition.toLowerCase(Locale.ENGLISH));

    return TIMELINESS_REPORT.equals(template.getType())
        ? jasperReportsViewService.getTimelinessJasperReportView(jasperView, map)
        : new ModelAndView(jasperView, map);
  }

}
