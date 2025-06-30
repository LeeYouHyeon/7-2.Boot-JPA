package com.example.bootJPA.dto;

import lombok.*;

import java.time.LocalDateTime;

import static com.example.bootJPA.dto.TimeConverter.timeOrDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private Long bno;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime regDate, modDate;
    private long readCount;
    private int cmtCount;

    public String getRegTimeOrDate() {
        return timeOrDate(regDate);
    }

    public String getModTimeOrDate() {
        if (regDate.equals(modDate)) return "-";
        return timeOrDate(modDate);
    }

    public String getCmtCountPrint() {
        return "[" + cmtCount + "]";
    }
}
