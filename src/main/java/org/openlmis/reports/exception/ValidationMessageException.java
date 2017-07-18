package org.openlmis.reports.exception;

import org.openlmis.reports.utils.Message;

public class ValidationMessageException extends BaseMessageException {

  public ValidationMessageException(Message message) {
    super(message);
  }

  public ValidationMessageException(Message message, Throwable cause) {
    super(message, cause);
  }

  public ValidationMessageException(String messageKey) {
    super(messageKey);
  }
}
