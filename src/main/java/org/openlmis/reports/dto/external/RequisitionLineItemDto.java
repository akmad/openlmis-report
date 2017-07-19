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

package org.openlmis.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RequisitionLineItemDto {

  public static final String BEGINNING_BALANCE = "beginningBalance";
  public static final String ADJUSTED_CONSUMPTION = "adjustedConsumption";
  public static final String AVERAGE_CONSUMPTION = "averageConsumption";

  private UUID id;
  private OrderableDto orderable;
  private Integer beginningBalance;
  private Integer totalReceivedQuantity;
  private Integer totalLossesAndAdjustments;
  private Integer stockOnHand;
  private Integer requestedQuantity;
  private Integer totalConsumedQuantity;
  private String requestedQuantityExplanation;
  private String remarks;
  private Integer approvedQuantity;
  private Integer totalStockoutDays;
  private Integer total;
  private Long packsToShip;
  private BigDecimal pricePerPack;
  private Integer numberOfNewPatientsAdded;
  private BigDecimal totalCost;
  private Boolean skipped;
  private Integer adjustedConsumption;
  private List<Integer> previousAdjustedConsumptions;
  private Integer averageConsumption;
  private BigDecimal maxPeriodsOfStock;
  private Integer maximumStockQuantity;
  private Integer calculatedOrderQuantity;
  private String orderableCategoryDisplayName;

  @JsonProperty
  private List<StockAdjustmentDto> stockAdjustments;

  @JsonIgnore
  public Boolean isNonFullSupply(ProgramDto programDto) {
    return !orderable.getProgramOrderable(programDto).getFullSupply();
  }
}
