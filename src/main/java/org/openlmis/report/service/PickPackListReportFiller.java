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

package org.openlmis.report.service;

import org.openlmis.report.dto.PickPackEntity;
import org.openlmis.report.dto.PickPackEntityLineItem;
import org.openlmis.report.dto.external.fulfillment.OrderDto;
import org.openlmis.report.dto.external.fulfillment.OrderLineItemDto;
import org.openlmis.report.dto.external.fulfillment.ShipmentDraftDto;
import org.openlmis.report.dto.external.fulfillment.ShipmentLineItemDto;
import org.openlmis.report.dto.external.stockmanagement.StockCardDto;
import org.openlmis.report.service.fulfillment.OrderFulfillmentDataService;
import org.openlmis.report.service.fulfillment.ShipmentDraftFulfillmentDataService;
import org.openlmis.report.service.stockmanagement.StockCardStockManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PickPackListReportFiller implements ReportFiller {
  private static final String REPORT_NAME = "Pick Pack List";

  @Autowired
  private OrderFulfillmentDataService orderFulfillmentDataService;

  @Autowired
  private ShipmentDraftFulfillmentDataService shipmentDraftFulfillmentDataService;

  @Autowired
  private StockCardStockManagementService stockCardStockManagementService;

  @Override
  public boolean support(String reportName) {
    return REPORT_NAME.equals(reportName);
  }

  @Override
  public void fillParameters(Map<String, Object> parameters) {
    String shipmentDraftId = parameters.get("shipmentDraftId").toString();
    PickPackEntity entity = getEntity(shipmentDraftId);

    parameters.put("entity", entity);
    parameters.put("lineItems", entity.getLineItems());
  }

  private PickPackEntity getEntity(String shipmentDraftId) {
    ShipmentDraftDto shipmentDraft = shipmentDraftFulfillmentDataService.findOne(
        UUID.fromString(shipmentDraftId)
    );

    OrderDto order = orderFulfillmentDataService.findOne(
        shipmentDraft.getOrder().getId()
    );

    List<StockCardDto> stockCards = stockCardStockManagementService.getStockCards(
        order.getSupplyingFacility().getId(), order.getProgram().getId()
    );

    List<PickPackEntityLineItem> lineItems = shipmentDraft
        .getLineItems()
        .stream()
        .map(line -> createLineItems(line, order, stockCards))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    return new PickPackEntity(order, lineItems);
  }

  private List<PickPackEntityLineItem> createLineItems(ShipmentLineItemDto line,
                                                       OrderDto order,
                                                       List<StockCardDto> stockCards) {
    for (OrderLineItemDto orderLine : order.getOrderLineItems()) {
      if (Objects.equals(orderLine.getOrderable().getId(), line.getOrderable().getId())) {
        List<StockCardDto> cards = getStockCards(line, stockCards);

        if (cards.isEmpty()) {
          return Collections.singletonList(new PickPackEntityLineItem(
              orderLine.getOrderable(), null, line.getQuantityShipped())
          );
        }

        return cards
            .stream()
            .map(card -> new PickPackEntityLineItem(
                orderLine.getOrderable(), card, line.getQuantityShipped()))
            .collect(Collectors.toList());
      }
    }

    return Collections.emptyList();
  }

  private List<StockCardDto> getStockCards(ShipmentLineItemDto line,
                                           List<StockCardDto> stockCards) {
    return stockCards
        .stream()
        .filter(card -> Objects.equals(line.getOrderable().getId(), card.getOrderable().getId()))
        .collect(Collectors.toList());
  }

}
