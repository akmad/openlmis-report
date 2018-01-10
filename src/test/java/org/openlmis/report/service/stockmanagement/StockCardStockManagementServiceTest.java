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

package org.openlmis.report.service.stockmanagement;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.openlmis.report.dto.external.stockmanagement.StockCardDto;
import org.openlmis.report.service.BaseCommunicationService;
import org.openlmis.report.utils.DynamicPageTypeReference;
import org.openlmis.report.utils.PageImplRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StockCardStockManagementServiceTest
    extends BaseStockManagementServiceTest<StockCardDto> {

  private static final String URI_QUERY_NAME = "name";
  private static final String URI_QUERY_VALUE = "value";

  @Override
  protected StockCardDto generateInstance() {
    return new StockCardDto();
  }

  @Override
  protected BaseCommunicationService<StockCardDto> getService() {
    return new StockCardStockManagementService();
  }

  @Test
  public void shouldGetStockCardsBasedOnFacilityAndProgram() {
    // given
    StockCardStockManagementService service = (StockCardStockManagementService) prepareService();
    StockCardDto instance = generateInstance();

    PageImplRepresentation<StockCardDto> page = new PageImplRepresentation<>();
    page.setContent(Collections.singletonList(instance));

    ResponseEntity response = new ResponseEntity(page, HttpStatus.OK);

    UUID facility = UUID.randomUUID();
    UUID program = UUID.randomUUID();

    // when
    when(restTemplate.exchange(
        any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
        any(DynamicPageTypeReference.class)
    )).thenReturn(response);

    List<StockCardDto> found = service.getStockCards(facility, program);

    // then
    assertThat(found, hasSize(1));
    assertThat(found, hasItem(instance));

    verify(restTemplate).exchange(
        uriCaptor.capture(), eq(HttpMethod.GET), entityCaptor.capture(),
        any(DynamicPageTypeReference.class)
    );

    URI uri = uriCaptor.getValue();
    assertThat(uri.toString(), startsWith(service.getServiceUrl()));
    assertThat(uri.toString(), containsString(service.getUrl()));

    List<NameValuePair> parse = URLEncodedUtils.parse(uri, Charset.forName("UTF-8"));

    assertThat(parse, hasItem(allOf(
        hasProperty(URI_QUERY_NAME, is("size")),
        hasProperty(URI_QUERY_VALUE, is(String.valueOf(Integer.MAX_VALUE))))
    ));
    assertThat(parse, hasItem(allOf(
        hasProperty(URI_QUERY_NAME, is("facility")),
        hasProperty(URI_QUERY_VALUE, is(facility.toString())))
    ));
    assertThat(parse, hasItem(allOf(
        hasProperty(URI_QUERY_NAME, is("program")),
        hasProperty(URI_QUERY_VALUE, is(program.toString())))
    ));

    HttpEntity entity = entityCaptor.getValue();

    assertAuthHeader(entity);
    assertThat(entity.getBody(), is(nullValue()));
  }
}
