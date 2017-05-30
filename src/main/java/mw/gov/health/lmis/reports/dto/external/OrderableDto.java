package mw.gov.health.lmis.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class OrderableDto {
  private UUID id;
  private String productCode;
  private String fullProductName;
  private long netContent;
  private long packRoundingThreshold;
  private boolean roundToZero;
  private Set<ProgramOrderableDto> programs;
  private DispensableDto dispensable;

  /**
   * Get program orderable for given order
   * @return program orderable.
   */
  @JsonIgnore
  public ProgramOrderableDto getProgramOrderable(OrderDto order) {
    return programs.stream().filter(po -> po.getProgramId().equals(order.getProgram().getId()))
            .findFirst().orElse(null);
  }
}
