package org.openlmis.reports.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

import org.openlmis.reports.domain.JasperTemplate;

public interface JasperTemplateRepository
    extends PagingAndSortingRepository<JasperTemplate, UUID> {
  JasperTemplate findByName(@Param("name") String name);
}
