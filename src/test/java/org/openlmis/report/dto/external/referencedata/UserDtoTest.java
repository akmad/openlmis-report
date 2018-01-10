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

package org.openlmis.report.dto.external.referencedata;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.dto.external.DtoGenerator;

import java.util.Set;

public class UserDtoTest {

  private UserDto userDto = new UserDto();

  @Before
  public void setUp() {
    userDto.setUsername("jdoe");
  }

  @Test
  public void shouldPrintNameAsFirstLastName() {
    userDto.setFirstName("John");
    userDto.setLastName("Doe");

    assertEquals("John Doe", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsOnlyFirstName() {
    userDto.setFirstName("John");

    assertEquals("John", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsOnlyLastName() {
    userDto.setLastName("Doe");

    assertEquals("Doe", userDto.printName());
  }

  @Test
  public void shouldPrintNameAsUsername() {
    assertEquals("jdoe", userDto.printName());
  }

  @Test
  public void equalsContract() {
    Pair<RoleAssignmentDto, RoleAssignmentDto> assignmentPair = DtoGenerator
        .of(RoleAssignmentDto.class);

    EqualsVerifier
        .forClass(UserDto.class)
        .withPrefabValues(Set.class, emptySet(), singleton(assignmentPair.getLeft()))
        .suppress(Warning.NONFINAL_FIELDS) // fields in dto cannot be final
        .verify();
  }

}
