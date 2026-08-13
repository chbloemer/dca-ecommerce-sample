package de.sample.aiarchitecture.account.adapter.incoming.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Reads a date out of a submitted form field.
 *
 * <p>Bound as a {@code String} rather than as a {@code LocalDate} on purpose: letting Spring
 * convert the parameter would turn an empty or malformed value into a binding failure and a bare
 * 400, while every other rejected value on these pages is answered with the form and a message.
 * Parsing here keeps all rejections on the same path.
 */
final class SubmittedDate {

  /** Shown when the value cannot be read as a date; names the format the date input submits. */
  static final String NOT_A_DATE = "Please enter a date like 1990-05-17";

  private SubmittedDate() {}

  /**
   * Parses a submitted date field.
   *
   * @param submitted the raw field value, as the browser sent it
   * @return the date, or empty if the field was blank or is not an ISO date
   */
  static Optional<LocalDate> parse(final String submitted) {
    if (submitted == null || submitted.isBlank()) {
      return Optional.empty();
    }
    try {
      return Optional.of(LocalDate.parse(submitted.trim()));
    } catch (final DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
