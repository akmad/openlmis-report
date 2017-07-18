package org.openlmis.reports.service;

import static org.openlmis.reports.i18n.PermissionMessageKeys.ERROR_NO_PERMISSION;

import org.openlmis.reports.dto.external.DetailedRoleAssignmentDto;
import org.openlmis.reports.dto.external.RequisitionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import org.openlmis.reports.dto.external.ResultDto;
import org.openlmis.reports.dto.external.RightDto;
import org.openlmis.reports.dto.external.UserDto;
import org.openlmis.reports.exception.PermissionMessageException;
import org.openlmis.reports.service.referencedata.UserReferenceDataService;
import org.openlmis.reports.utils.AuthenticationHelper;
import org.openlmis.reports.utils.Message;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {
  public static final String REPORT_TEMPLATES_EDIT = "REPORT_TEMPLATES_EDIT";
  public static final String REPORTS_VIEW = "REPORTS_VIEW";
  public static final String ORDERS_VIEW = "ORDERS_VIEW";
  public static final UUID AGGREGATE_ORDERS_ID =
          UUID.fromString("f28d0ebd-7276-4453-bc3c-48556a4bd25a");
  public static final UUID ORDER_ID =
          UUID.fromString("3c9d1e80-1e45-4adb-97d9-208b6fdceeec");
  public static final String REQUISITION_VIEW = "REQUISITION_VIEW";

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  public void canEditReportTemplates() {
    checkPermission(REPORT_TEMPLATES_EDIT);
  }

  /**
   * Check whether the user has REPORTS_VIEW permission.
   * @param templateId (optional) id of the report; if it equals to Aggregate Orders,
   *                   the user can have either the ORDERS_VIEW or REPORTS_VIEW permission
   */
  public void canViewReports(UUID templateId) {
    if (templateId != null && (templateId.equals(AGGREGATE_ORDERS_ID)
            || templateId.equals(ORDER_ID))) {
      canViewReportsOrOrders();
    } else {
      checkPermission(REPORTS_VIEW);
    }
  }

  /**
   * Checks if current user has permission to view a requisition.
   */
  public void canViewRequisition(RequisitionDto requisition) {
    checkPermission(REQUISITION_VIEW, requisition.getProgram().getId(),
            requisition.getFacility().getId(), null);
  }

  public void canViewReportsOrOrders() {
    checkAnyPermission(Arrays.asList(REPORTS_VIEW, ORDERS_VIEW));
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightName));
    }
  }

  private void checkPermission(String rightName, UUID program, UUID facility, UUID warehouse) {
    if (!hasPermission(rightName, program, facility, warehouse)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightName));
    }
  }

  private void checkAnyPermission(List<String> rightNames) {
    if (rightNames.stream().noneMatch(this::hasPermission)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightNames));
    }
  }

  private Boolean hasPermission(String rightName) {
    if (ORDERS_VIEW.equals(rightName)) {
      return hasFulfillmentPermission(rightName);
    }
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(user.getId(), right.getId());
    return null != result && result.getResult();
  }

  private Boolean hasPermission(String rightName, UUID program, UUID facility, UUID warehouse) {
    OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
            .getAuthentication();
    if (authentication.isClientOnly()) {
      return true;
    }
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(
            user.getId(), right.getId(), program, facility, warehouse
    );
    return null != result && result.getResult();
  }

  // Check if a user has fulfillment permission without specifying the warehouse
  private Boolean hasFulfillmentPermission(String rightName) {
    UserDto user = authenticationHelper.getCurrentUser();
    List<DetailedRoleAssignmentDto> roleAssignments =
            userReferenceDataService.getUserRightsAndRoles(user.getId());

    return roleAssignments.stream().anyMatch(
        assignment -> assignment.getRole().getRights().stream().anyMatch(
            right -> right.getName().equals(rightName)
    ));
  }
}
