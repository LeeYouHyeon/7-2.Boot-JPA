package com.example.bootJPA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing // JPA를 사용하기 위한 annotation
@SpringBootApplication
public class BootJpaApplication {

  public static void main(String[] args) {
    SpringApplication.run(BootJpaApplication.class, args);
  }

}
