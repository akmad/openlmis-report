package mw.gov.health.lmis.reports.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StockAdjustmentReasonDto {
  private UUID id;
  private ProgramDto program;
  private String name;
  private String description;
  private Boolean additive;
  private Integer displayOrder;
}
