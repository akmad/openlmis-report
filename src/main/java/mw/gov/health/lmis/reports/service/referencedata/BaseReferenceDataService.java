package mw.gov.health.lmis.reports.service.referencedata;

import org.springframework.beans.factory.annotation.Value;

import mw.gov.health.lmis.reports.service.BaseCommunicationService;

public abstract class BaseReferenceDataService<T> extends BaseCommunicationService<T> {

  @Value("${referencedata.url}")
  private String referenceDataUrl;

  @Override
  protected String getServiceUrl() {
    return referenceDataUrl;
  }
}
