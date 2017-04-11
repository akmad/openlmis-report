package mw.gov.health.lmis.reports.service.referencedata;

import org.springframework.stereotype.Service;

import java.util.List;

import mw.gov.health.lmis.reports.dto.external.RightDto;
import mw.gov.health.lmis.utils.RequestParameters;

@Service
public class RightReferenceDataService extends BaseReferenceDataService<RightDto> {

  @Override
  protected String getUrl() {
    return "/api/rights/";
  }

  @Override
  protected Class<RightDto> getResultClass() {
    return RightDto.class;
  }

  @Override
  protected Class<RightDto[]> getArrayResultClass() {
    return RightDto[].class;
  }

  /**
   * Find a correct right by the provided name.
   *
   * @param name right name
   * @return right related with the name or {@code null}.
   */
  public RightDto findRight(String name) {
    List<RightDto> rights = findAll("search", RequestParameters.init().set("name", name));
    return rights.isEmpty() ? null : rights.get(0);
  }
}
