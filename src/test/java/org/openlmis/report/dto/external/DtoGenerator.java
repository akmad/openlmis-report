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

package org.openlmis.report.dto.external;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class DtoGenerator {
  private static Map<Class<?>, Pair> REFERENCES = new HashMap<>();
  private static final Random RANDOM = new Random();

  private DtoGenerator() {
    throw new UnsupportedOperationException();
  }

  public static <T> Pair<T, T> of(Class<T> clazz) {
    return (Pair<T, T>) REFERENCES.computeIfAbsent(clazz, DtoGenerator::generatePair);
  }

  private static <T> ImmutablePair<T, T> generatePair(Class<T> clazz) {
    return new ImmutablePair<>(generate(clazz), generate(clazz));
  }

  private static <T> T generate(Class<T> clazz) {
    T instance;

    try {
      instance = clazz.newInstance();
    } catch (Exception exp) {
      throw new IllegalStateException("Missing no args constructor", exp);
    }

    for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
      if ("class".equals(descriptor.getName())) {
        continue;
      }

      try {
        Object value = generateValue(clazz, descriptor.getPropertyType());
        PropertyUtils.setProperty(instance, descriptor.getName(), value);
      } catch (Exception exp) {
        throw new IllegalStateException(
            "Can't set value for property: " + descriptor.getName(), exp
        );
      }
    }

    return instance;
  }

  private static Object generateValue(Class<?> type, Class<?> propertyType) {
    Object value = generateBaseValue(propertyType);
    value = null == value ? generateCollectionValue(propertyType) : value;

    if (null != value) {
      return value;
    }

    if (type.equals(propertyType)) {
      // if types are equals it means that the given DTO contains a element which is represent as
      // a child or parent. For now we return null.
      return null;
    }

    return of(propertyType).getLeft();
  }

  private static Object generateCollectionValue(Class<?> propertyType) {
    if (List.class.isAssignableFrom(propertyType)) {
      return Collections.emptyList();
    }

    if (Set.class.isAssignableFrom(propertyType)) {
      return Collections.emptySet();
    }

    if (Map.class.isAssignableFrom(propertyType)) {
      return Collections.emptyMap();
    }

    return null;
  }

  private static Object generateBaseValue(Class<?> propertyType) {
    if (String.class.isAssignableFrom(propertyType)) {
      return RandomStringUtils.randomAlphanumeric(10);
    }

    if (Number.class.isAssignableFrom(propertyType)) {
      return propertyType.cast(RANDOM.nextInt(1000));
    }

    if (UUID.class.isAssignableFrom(propertyType)) {
      return UUID.randomUUID();
    }

    if (Boolean.class.isAssignableFrom(propertyType)
        || boolean.class.isAssignableFrom(propertyType)) {
      return true;
    }

    if (LocalDate.class.isAssignableFrom(propertyType)) {
      return LocalDate.now();
    }

    if (Enum.class.isAssignableFrom(propertyType)) {
      int idx = RANDOM.nextInt(propertyType.getEnumConstants().length);
      return propertyType.getEnumConstants()[idx];
    }

    return null;
  }

}
