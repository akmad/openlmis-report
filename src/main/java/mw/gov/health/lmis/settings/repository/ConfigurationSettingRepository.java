package mw.gov.health.lmis.settings.repository;

import mw.gov.health.lmis.reports.repository.ReferenceDataRepository;
import mw.gov.health.lmis.settings.domain.ConfigurationSetting;

public interface ConfigurationSettingRepository
    extends ReferenceDataRepository<ConfigurationSetting, String> {
}
