package com.example.bootJPA.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends TimeBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long cno;

    @Column(nullable = false)
    private Long bno;

    @Column(length = 200, nullable = false)
    private String writer;

    @Column(length = 1000)
    private String content;

    private Long parent;

    @Column(name = "reply_count", nullable = false)
    private Long replyCount;
}
