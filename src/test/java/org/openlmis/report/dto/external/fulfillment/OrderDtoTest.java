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

package org.openlmis.report.dto.external.fulfillment;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.openlmis.report.dto.external.DtoGenerator;
import org.openlmis.report.dto.external.referencedata.FacilityDto;
import org.openlmis.report.dto.external.referencedata.UserDto;

public class OrderDtoTest {

  @Test
  public void equalsContract() {
    Pair<FacilityDto, FacilityDto> facilityPair = DtoGenerator.of(FacilityDto.class);
    Pair<UserDto, UserDto> userPair = DtoGenerator.of(UserDto.class);

    EqualsVerifier
        .forClass(OrderDto.class)
        .withPrefabValues(FacilityDto.class, facilityPair.getLeft(), facilityPair.getRight())
        .withPrefabValues(UserDto.class, userPair.getLeft(), userPair.getRight())
        .suppress(Warning.NONFINAL_FIELDS) // fields in dto cannot be final
        .verify();
  }

}
