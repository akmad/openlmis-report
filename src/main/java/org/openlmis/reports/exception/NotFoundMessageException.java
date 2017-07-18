package org.openlmis.reports.exception;

import org.openlmis.reports.utils.Message;

/**
 * Exception thrown when resource was not found.
 */
public class NotFoundMessageException extends BaseMessageException {
  public NotFoundMessageException(Message message) {
    super(message);
  }
}
