package org.openlmis.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StockAdjustmentReasonDto {
  private UUID id;
  private String name;
  private String description;
  private Boolean additive;
  private Integer displayOrder;
  private ProgramDto program;
}
