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
public class FileDTO {
    private String uuid;
    private String saveDir;
    private String fileName;
    private int fileType;
    private long bno;
    private long fileSize;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    public String getRegTimeOrDate() {
        return timeOrDate(regDate);
    }

    public String getModTimeOrDate() {
        if (regDate.equals(modDate)) return "-";
        return timeOrDate(modDate);
    }

    public String getCustomFileSize() {
        double value = fileSize;
        if (value < 1024) return fileSize + " Bytes";

        value /= 1024;
        if (value < 1024) return String.format("%.2f KiB", value);
        return String.format("%.2f MiB", value / 1024);
    }
}
