package com.example.bootJPA.entity;

import jakarta.persistence.*;
import lombok.*;

/* Entity : DB의 테이블 클래스
* DTO : 객체를 생성하는 클래스
* JPA Auditing : reg_date, mod_date처럼 등록일과 수정일같이
모든 클래스에서 동일하게 사용하는 칼럼을 별도로 관리. => base class
* @id = primary key
* 기본키 생성 전략 : GeneratedValue
* auto_increment => GenerationType.IDENTITY
* 
* @Column(설정=값) (생략 가능)
* */

// @Table(name = "board"). 테이블명 설정이 없으면 Entity 클래스명과 동일하게 설정
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends TimeBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String writer;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(name="read_count")
    private long readCount;

    @Column(name="cmt_count")
    private int cmtCount;
}
