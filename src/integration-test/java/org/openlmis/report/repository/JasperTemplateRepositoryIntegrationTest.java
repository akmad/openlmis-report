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

package org.openlmis.report.repository;

import org.junit.Test;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.JasperTemplateParameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class JasperTemplateRepositoryIntegrationTest extends
    BaseCrudRepositoryIntegrationTest<JasperTemplate> {

  private static final String NAME = "TemplateRepositoryIntegrationTest";

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Override
  JasperTemplateRepository getRepository() {
    return this.jasperTemplateRepository;
  }

  @Override
  protected JasperTemplate generateInstance() {
    JasperTemplate jasperTemplate = new JasperTemplate();
    jasperTemplate.setName(NAME);
    return jasperTemplate;
  }

  @Test
  public void shouldFindTemplateByName() {
    // given
    jasperTemplateRepository.save(generateInstance());

    // when
    JasperTemplate found = jasperTemplateRepository.findByName(NAME);

    // then
    assertThat(found.getName(), is(NAME));
  }

  @Test
  public void shouldBindParametersToTemplateOnSave() {
    // given
    JasperTemplateParameter templateParameter = new JasperTemplateParameter();
    templateParameter.setName("parameter");
    templateParameter.setRequired(true);

    JasperTemplate template = generateInstance();
    template.setTemplateParameters(Collections.singletonList(templateParameter));

    // when
    JasperTemplate result = jasperTemplateRepository.save(template);

    // then
    assertEquals(templateParameter.getTemplate().getId(), result.getId());
  }

}
