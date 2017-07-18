package org.openlmis.reports.dto.external;

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
    return getProgramOrderable(order.getProgram());
  }

  /**
   * Get program orderable for given program
   * @return program orderable.
   */
  @JsonIgnore
  public ProgramOrderableDto getProgramOrderable(ProgramDto program) {
    return programs.stream().filter(po -> po.getProgramId().equals(program.getId()))
            .findFirst().orElse(null);
  }
}
