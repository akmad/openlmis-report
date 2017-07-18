package org.openlmis.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TimelinessReportFacilityDto extends FacilityDto implements FacilityDto.Exporter {

  public static final Integer DISTRICT_LEVEL = 3;

  /**
   * Get zone of the facility that has the district level.
   * @return district of the facility.
   */
  @JsonIgnore
  public GeographicZoneDto getThirdLevel() {
    return getZoneByLevelNumber(DISTRICT_LEVEL);
  }
}
