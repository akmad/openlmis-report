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

package org.openlmis.report.security;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.beans.factory.annotation.Autowired;

import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.utils.AuthenticationHelper;

/**
 * This class is used by JaVers to retrieve the name of the user currently logged in.
 * JaVers then associates audited changes being made with this particular user.
 */
public class UserNameProvider implements AuthorProvider {

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Override
  public String provide() {
    try {
      UserDto currentUser = authenticationHelper.getCurrentUser();
      if (currentUser != null && currentUser.getId() != null) {
        return currentUser.getId().toString();
      } else {
        return "unauthenticated user";
      }
    } catch (Exception ex) {
      return "unknown user";
    }
  }
}
