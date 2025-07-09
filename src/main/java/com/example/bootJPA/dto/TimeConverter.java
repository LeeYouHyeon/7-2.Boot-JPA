package com.example.bootJPA.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class TimeConverter {
  public static String timeOrDate(LocalDateTime t) {
    String date = t.toLocalDate().toString(),
        time = t.toLocalTime().toString().substring(0, 8);
    if (LocalDate.now().toString().equals(date)) return time;
    return date;
  }
}
