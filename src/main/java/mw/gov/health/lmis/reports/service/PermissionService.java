package mw.gov.health.lmis.reports.service;

import static mw.gov.health.lmis.reports.i18n.PermissionMessageKeys.ERROR_NO_PERMISSION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mw.gov.health.lmis.reports.dto.external.ResultDto;
import mw.gov.health.lmis.reports.dto.external.RightDto;
import mw.gov.health.lmis.reports.dto.external.UserDto;
import mw.gov.health.lmis.reports.exception.PermissionMessageException;
import mw.gov.health.lmis.reports.service.referencedata.UserReferenceDataService;
import mw.gov.health.lmis.utils.AuthenticationHelper;
import mw.gov.health.lmis.utils.Message;

@SuppressWarnings("PMD.TooManyMethods")
@Service
public class PermissionService {
  public static final String REPORT_TEMPLATES_EDIT = "REPORT_TEMPLATES_EDIT";
  public static final String REPORTS_VIEW = "REPORTS_VIEW";

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  public void canEditReportTemplates() {
    checkPermission(REPORT_TEMPLATES_EDIT);
  }

  public void canViewReports() {
    checkPermission(REPORTS_VIEW);
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
      throw new PermissionMessageException(new Message(ERROR_NO_PERMISSION, rightName));
    }
  }

  private Boolean hasPermission(String rightName) {
    UserDto user = authenticationHelper.getCurrentUser();
    RightDto right = authenticationHelper.getRight(rightName);
    ResultDto<Boolean> result = userReferenceDataService.hasRight(user.getId(), right.getId());
    return null != result && result.getResult();
  }
}
