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

package org.openlmis.reports.service.referencedata;

import org.openlmis.reports.dto.external.StockAdjustmentReasonDto;
import org.openlmis.reports.utils.RequestParameters;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class StockAdjustmentReasonReferenceDataService
    extends BaseReferenceDataService<StockAdjustmentReasonDto> {

  @Override
  protected String getUrl() {
    return "/api/stockAdjustmentReasons/";
  }

  @Override
  protected Class<StockAdjustmentReasonDto> getResultClass() {
    return StockAdjustmentReasonDto.class;
  }

  @Override
  protected Class<StockAdjustmentReasonDto[]> getArrayResultClass() {
    return StockAdjustmentReasonDto[].class;
  }

  /**
   * Retrieves StockAdjustmentReasons for a specified program
   *
   * @param programId the program id.
   * @return a list of StockAdjustmentReasons.
   */
  public Collection<StockAdjustmentReasonDto> search(UUID programId) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("program", programId);

    return findAll("search", parameters);
  }
}
