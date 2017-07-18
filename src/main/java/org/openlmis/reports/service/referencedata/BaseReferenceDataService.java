package org.openlmis.reports.service.referencedata;

import org.springframework.beans.factory.annotation.Value;

import org.openlmis.reports.service.BaseCommunicationService;

public abstract class BaseReferenceDataService<T> extends BaseCommunicationService<T> {

  @Value("${referencedata.url}")
  private String referenceDataUrl;

  @Override
  protected String getServiceUrl() {
    return referenceDataUrl;
  }
}
