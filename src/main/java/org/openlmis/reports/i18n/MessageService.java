package org.openlmis.reports.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import org.openlmis.reports.utils.Message;

@Service
public class MessageService {

  @Autowired
  private ExposedMessageSource messageSource;

  public Message.LocalizedMessage localize(Message message) {
    return message.localMessage(messageSource, LocaleContextHolder.getLocale());
  }
}
