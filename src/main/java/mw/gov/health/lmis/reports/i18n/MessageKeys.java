package mw.gov.health.lmis.reports.i18n;

import java.util.Arrays;

public class MessageKeys {
  private static final String DELIMITER = ".";

  // General
  protected static final String SERVICE = "malawi.reports";
  protected static final String SERVICE_ERROR = join(SERVICE, "error");

  protected static final String NOT_FOUND = "notFound";

  protected static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }

  protected MessageKeys() {
    throw new UnsupportedOperationException();
  }
}
