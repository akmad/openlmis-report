package mw.gov.health.lmis.reports.service;

import static mw.gov.health.lmis.reports.i18n.PermissionMessageKeys.ERROR_NO_PERMISSION;

import mw.gov.health.lmis.reports.dto.external.DetailedRoleAssignmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.reports.dto.external.ResultDto;
import mw.gov.health.lmis.reports.dto.external.RightDto;
import mw.gov.health.lmis.reports.dto.external.UserDto;
import mw.gov.health.lmis.reports.exception.PermissionMessageException;
import mw.gov.health.lmis.reports.service.referencedata.UserReferenceDataService;
import mw.gov.health.lmis.utils.AuthenticationHelper;
import mw.gov.health.lmis.utils.Message;

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
    if (templateId != null && templateId.equals(AGGREGATE_ORDERS_ID)) {
      canViewReportsOrOrders();
    } else {
      checkPermission(REPORTS_VIEW);
    }
  }

  public void canViewReportsOrOrders() {
    checkAnyPermission(Arrays.asList(REPORTS_VIEW, ORDERS_VIEW));
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
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
