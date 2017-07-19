/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.report.web;

import org.openlmis.report.dto.external.GeographicZoneDto;
import org.openlmis.report.dto.external.ProcessingPeriodDto;
import org.openlmis.report.dto.external.ProgramDto;
import org.openlmis.report.dto.external.RequisitionDto;
import org.openlmis.report.dto.external.StockAdjustmentReasonDto;
import org.openlmis.report.exception.JasperReportViewException;
import org.openlmis.report.exception.NotFoundMessageException;
import org.openlmis.report.i18n.MessageKeys;
import org.openlmis.report.service.JasperReportsViewService;
import org.openlmis.report.service.PermissionService;
import org.openlmis.report.service.referencedata.GeographicZoneReferenceDataService;
import org.openlmis.report.service.referencedata.PeriodReferenceDataService;
import org.openlmis.report.service.referencedata.ProgramReferenceDataService;
import org.openlmis.report.service.referencedata.StockAdjustmentReasonReferenceDataService;
import org.openlmis.report.service.requisition.RequisitionService;
import org.openlmis.report.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.UUID;

@Controller
@Transactional
@RequestMapping("/api/report")
public class ReportsController extends BaseController {
  private static int DISTRICT_LEVEL = 3;

  @Autowired
  private GeographicZoneReferenceDataService geographicZoneReferenceDataService;

  @Autowired
  private PeriodReferenceDataService periodReferenceDataService;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  @Autowired
  private StockAdjustmentReasonReferenceDataService stockAdjustmentReasonReferenceDataService;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private JasperReportsViewService jasperReportsViewService;

  @Autowired
  private RequisitionService requisitionService;

  /**
   * Print out requisition as a PDF file.
   *
   * @param id The UUID of the requisition to print
   * @return ResponseEntity with the "#200 OK" HTTP response status and PDF file on success, or
   *     ResponseEntity containing the error description status.
   */
  @RequestMapping(value = "/requisitions/{id}/print", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView print(HttpServletRequest request, @PathVariable("id") UUID id)
          throws JasperReportViewException {
    RequisitionDto requisition = requisitionService.findOne(id);

    if (requisition == null) {
      throw new NotFoundMessageException(
              new Message(MessageKeys.ERROR_REQUISITION_NOT_FOUND, id));
    }
    permissionService.canViewRequisition(requisition);

    return jasperReportsViewService.getRequisitionJasperReportView(requisition, request);
  }

  /**
   * Get all districts.
   *
   * @return districts.
   */
  @RequestMapping(value = "/districts", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Collection<GeographicZoneDto> getDistricts() {
    permissionService.canViewReportsOrOrders();
    return geographicZoneReferenceDataService.search(DISTRICT_LEVEL, null);
  }

  /**
   * Get all processing periods.
   *
   * @return processing periods.
   */
  @RequestMapping(value = "/processingPeriods", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Collection<ProcessingPeriodDto> getProcessingPeriods() {
    permissionService.canViewReportsOrOrders();
    return periodReferenceDataService.findAll();
  }

  /**
   * Get all programs.
   *
   * @return programs.
   */
  @RequestMapping(value = "/programs", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Collection<ProgramDto> getPrograms() {
    permissionService.canViewReportsOrOrders();
    return programReferenceDataService.findAll();
  }

  /**
   * Retrieves StockAdjustmentReasons for a specified program
   *
   * @param programId the program id.
   * @return a list of StockAdjustmentReasons.
   */
  @RequestMapping(value = "/stockAdjustmentReasons/search", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Collection<StockAdjustmentReasonDto> findStockAdjustmentReasonsByProgramId(
          @RequestParam("program") UUID programId) {
    permissionService.canViewReports(null);
    return stockAdjustmentReasonReferenceDataService.search(programId);
  }
}
