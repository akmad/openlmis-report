/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.report.dto.external.stockmanagement;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.openlmis.report.dto.external.FacilityDto;
import org.openlmis.report.dto.external.ProgramDto;
import org.openlmis.report.dto.external.referencedata.LotDto;
import org.openlmis.report.dto.external.referencedata.OrderableDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCardDto {
  private UUID id;
  private Integer stockOnHand;
  private FacilityDto facility;
  private ProgramDto program;
  private OrderableDto orderable;
  private LotDto lot;
  private Map<String, String> extraData;
  @JsonFormat(shape = STRING)
  private LocalDate lastUpdate;
}
