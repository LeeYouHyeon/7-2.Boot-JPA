package com.example.bootJPA.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BoardFileDTO {
    private BoardDTO boardDTO;
    private List<FileDTO> fileList;

    public long getTotalSize() {
        if (fileList == null) return 0L;

        long answer = 0L;
        for (FileDTO fileDTO : fileList) answer += fileDTO.getFileSize();
        return answer;
    }
}
