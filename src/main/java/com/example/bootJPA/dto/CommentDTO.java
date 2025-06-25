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
public class CommentDTO {
    private Long cno;
    private Long bno;
    private String writer;
    private String content;
    private LocalDateTime regDate, modDate;

    public String getRegTimeOrDate() {
        return timeOrDate(regDate);
    }

    public String getModTimeOrDate() {
        if (regDate.equals(modDate)) return "-";
        return timeOrDate(modDate);
    }
}
