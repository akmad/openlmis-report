package mw.gov.health.lmis.reports.service.requisition;

import mw.gov.health.lmis.reports.service.BaseCommunicationService;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseRequisitionService<T> extends BaseCommunicationService<T> {

  @Value("${requisition.url}")
  private String requisitionUrl;

  @Override
  protected String getServiceUrl() {
    return requisitionUrl;
  }
}
