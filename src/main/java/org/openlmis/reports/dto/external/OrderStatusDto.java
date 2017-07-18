package org.openlmis.reports.dto.external;

public enum OrderStatusDto {
  ORDERED,
  IN_TRANSIT,
  PICKING,
  PICKED,
  SHIPPED,
  RECEIVED,
  TRANSFER_FAILED,
  IN_ROUTE,
  READY_TO_PACK;
}
