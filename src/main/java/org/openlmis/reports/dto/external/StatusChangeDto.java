package org.openlmis.reports.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class StatusChangeDto {

  @Getter
  @Setter
  private RequisitionStatusDto status;

  @Getter
  @Setter
  private UUID authorId;

  @Getter
  @Setter
  private ZonedDateTime createdDate;

  @Setter
  private UserDto author;

  @JsonIgnore
  public UserDto getAuthor() {
    return author;
  }

  /**
   * Print createdDate for display purposes.
   * @return created date
   */
  @JsonIgnore
  public String printDate() {
    Locale locale = LocaleContextHolder.getLocale();
    String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);

    return dateTimeFormatter.format(createdDate);
  }
}
