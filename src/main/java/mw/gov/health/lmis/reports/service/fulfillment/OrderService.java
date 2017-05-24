package mw.gov.health.lmis.reports.service.fulfillment;

import mw.gov.health.lmis.reports.dto.external.OrderDto;
import mw.gov.health.lmis.reports.dto.external.OrderStatusDto;
import mw.gov.health.lmis.utils.RequestParameters;
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
