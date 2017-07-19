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

package org.openlmis.reports.service.fulfillment;

import org.openlmis.reports.dto.external.OrderDto;
import org.openlmis.reports.dto.external.OrderStatusDto;
import org.openlmis.reports.utils.RequestParameters;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService extends BaseFulfillmentService<OrderDto> {

  @Override
  protected String getUrl() {
    return "/api/orders/";
  }

  @Override
  protected Class<OrderDto> getResultClass() {
    return OrderDto.class;
  }

  @Override
  protected Class<OrderDto[]> getArrayResultClass() {
    return OrderDto[].class;
  }

  /**
   * Finds orders matching all of the provided parameters.
   */
  public Page<OrderDto> search(UUID supplyingFacility, UUID requestingFacility, UUID program,
                               UUID processingPeriod, Set<OrderStatusDto> statuses) {
    String commaDelimitedStatuses = (statuses == null) ? null :
            statuses.stream().map(Enum::name).collect(Collectors.joining(","));
    RequestParameters parameters = RequestParameters.init()
            .set("supplyingFacility", supplyingFacility)
            .set("requestingFacility", requestingFacility)
            .set("program", program)
            .set("processingPeriod", processingPeriod)
            .set("statuses", commaDelimitedStatuses);

    return getPage("search", parameters);
  }
}
