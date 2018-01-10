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

package org.openlmis.report.dto;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.openlmis.report.dto.external.referencedata.FacilityDto;
import org.openlmis.report.dto.external.fulfillment.OrderDto;

import lombok.Getter;

import java.util.List;

@Getter
public final class PickPackEntity {
  private final String orderCode;
  private final FacilityDto requestingFacility;
  private final FacilityDto supplyingFacility;
  private final JRBeanCollectionDataSource lineItems;

  /**
   * Create a new instance of {@link PickPackEntity} based on order details.
   */
  public PickPackEntity(OrderDto order, List<PickPackEntityLineItem> lineItems) {
    this.orderCode = order.getOrderCode();
    this.requestingFacility = order.getRequestingFacility();
    this.supplyingFacility = order.getSupplyingFacility();
    this.lineItems = new JRBeanCollectionDataSource(lineItems);
  }
}
