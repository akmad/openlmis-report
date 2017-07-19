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

package org.openlmis.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.reports.dto.external.RequisitionDto;
import org.openlmis.reports.dto.external.RequisitionLineItemDto;
import org.openlmis.reports.dto.external.UserDto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionReportDto {
  private RequisitionDto requisition;
  private List<RequisitionLineItemDto> fullSupply;
  private List<RequisitionLineItemDto> nonFullSupply;
  private BigDecimal fullSupplyTotalCost;
  private BigDecimal nonFullSupplyTotalCost;
  private BigDecimal totalCost;
  private UserDto initiatedBy;
  private ZonedDateTime initiatedDate;
  private UserDto submittedBy;
  private ZonedDateTime submittedDate;
  private UserDto authorizedBy;
  private ZonedDateTime authorizedDate;
}
