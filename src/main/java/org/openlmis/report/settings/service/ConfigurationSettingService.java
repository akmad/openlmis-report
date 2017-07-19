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

package org.openlmis.report.settings.service;

import static org.openlmis.report.settings.i18n.ConfigurationSettingMessageKeys.ERROR_CONFIGURATION_SETTING_NOT_FOUND;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import org.openlmis.report.exception.NotFoundMessageException;
import org.openlmis.report.settings.domain.ConfigurationSetting;
import org.openlmis.report.settings.repository.ConfigurationSettingRepository;
import org.openlmis.report.utils.Message;

@Service
@NoArgsConstructor
public class ConfigurationSettingService {

  @Autowired
  private ConfigurationSettingRepository configurationSettingRepository;

  /**
   * Return configuration setting with given key.
   *
   * @param key String value of key.
   * @return Configuration setting containing given key.
   * @throws NotFoundMessageException Exception saying that setting was not found.
   */
  public ConfigurationSetting getByKey(String key) {
    ConfigurationSetting setting = configurationSettingRepository.findOne(key);
    if (setting == null) {
      throw new NotFoundMessageException(
          new Message(ERROR_CONFIGURATION_SETTING_NOT_FOUND, key));
    }
    return setting;
  }

  /**
   * Return value for given key if possible.
   *
   * @param key String value indicates key.
   * @return String value of given key.
   */
  public String getStringValue(String key) {
    ConfigurationSetting configurationSetting = configurationSettingRepository.findOne(key);
    if (configurationSetting == null || configurationSetting.getValue() == null) {
      throw new NotFoundMessageException(new Message(ERROR_CONFIGURATION_SETTING_NOT_FOUND,
          key));
    }
    return configurationSetting.getValue();
  }

  /**
   * Return boolean value for given key.
   * If does not exist return false.
   *
   * @param key String value indicates key.
   * @return Boolean value of given key.
   */
  public Boolean getBoolValue(String key) {
    try {
      String value = getStringValue(key);
      return Boolean.parseBoolean(value);
    } catch (NotFoundMessageException exception) {
      return false;
    }
  }
}
