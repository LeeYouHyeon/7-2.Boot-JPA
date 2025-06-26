package com.example.bootJPA.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

//@Data : Getter~AllArgsConstructor. 권장하지 않음
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class File extends TimeBase {
    @Id
    private String uuid;

    @Column(name = "save_dir", nullable = false)
    private String saveDir;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false, columnDefinition = "int default 0")
    private int fileType;

    // @Column 어노테이션은 필수 아님
    private long bno;

    @Column(name = "file_size")
    private long fileSize;
}

/* 기본값 설정 방법
 * columnDefinition = "int default 0"
 * 일반적으론 ddl을 none으로 죽인 후 직접 DB에 가서 기본값을 추가함
 * */