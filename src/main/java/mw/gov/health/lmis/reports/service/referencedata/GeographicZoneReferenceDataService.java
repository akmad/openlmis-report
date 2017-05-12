package mw.gov.health.lmis.reports.service.referencedata;

import mw.gov.health.lmis.reports.dto.external.GeographicZoneDto;
import mw.gov.health.lmis.utils.RequestParameters;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class GeographicZoneReferenceDataService
    extends BaseReferenceDataService<GeographicZoneDto> {

  @Override
  protected String getUrl() {
    return "/api/geographicZones/";
  }

  @Override
  protected Class<GeographicZoneDto> getResultClass() {
    return GeographicZoneDto.class;
  }

  @Override
  protected Class<GeographicZoneDto[]> getArrayResultClass() {
    return GeographicZoneDto[].class;
  }

  /**
   * This method retrieves geographic zones filtered by geographic level and parent zone.
   *
   * @param levelNumber geographic level number
   * @param parent ID of parent geographic zone
   * @return List of matched geographic zones.
   */
  public Collection<GeographicZoneDto> search(Integer levelNumber, UUID parent) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("levelNumber", levelNumber)
        .set("parent", parent);

    return findAll("search", parameters);
  }
}
