package com.example.bootJPA.handler;

import com.example.bootJPA.entity.File;
import com.example.bootJPA.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
@EnableScheduling
public class FileSweeper {
  /* 매일 정해진 시간에 스케줄러를 실행
   * 매일 DB에 등록된 파일과 해당 일자의 폴더의 파일과 일치하는지 비교
   * DB에 있는 파일은 남기고, DB에 없는 파일은 삭제
   * 직접 DB에서 조회 : FileRepository 연결
   * */

  private final FileRepository fileRepository;
  private final String BASE_PATH = "D:\\web_0226_lyh\\_myProject\\_java\\_fileUpload\\";
  private final String separator = java.io.File.separator;

  // cron 방식 = 초 분 시 일 월 요일 년도(생략가능)
  @Scheduled(cron = "0 13 17 * * *")
  public void fileSweeper() {
    log.info(">>>>>>>>>> FileSweeper Start >> {}", LocalDateTime.now());
    List<File> dbList = fileRepository.findAll();

    // 파일 경로 + 파일명을 붙인 실제 파일 리스트 생성
    // D:\web_0226_lyh\_myProject\_java\_fileUpload\\2025\\06\\26\\uuid_fileName
    // D:\web_0226_lyh\_myProject\_java\_fileUpload\\2025\\06\\26\\uuid_th_fileName
    List<String> currFiles = new ArrayList<>();
    for (File fvo : dbList) {
      String filePath = BASE_PATH + fvo.getSaveDir() + separator + fvo.getUuid() + "_";
      currFiles.add(filePath + fvo.getFileName());
      if (fvo.getFileType() == 1) currFiles.add(filePath + "th_" + fvo.getFileName());
    }
    log.info(">>>>>> currFiles(DB) >> {}", currFiles);

    // 오늘 날짜의 파일 경로 설정
    LocalDate now = LocalDate.now();
    String today = now.toString();
    today = today.replace("-", separator);

    // 오늘 날짜 폴더 안에 있는 파일의 목록을 검색
    java.io.File dir = Paths.get(BASE_PATH + today).toFile();
    // 전체 객체를 배열로 나눠서 리턴
    java.io.File[] fileObject = dir.listFiles();
    if (fileObject == null) return;
    log.info(">>>>>> fileObject(폴더) >> {}", Arrays.toString(fileObject));

    // 실제 저장되어 있는 파일 목록과 DB의 파일을 비교
    // DB에 없는 파일을 삭제
    for (java.io.File file : fileObject) {
      String storedFileName = file.toPath().toString();
      if (currFiles.contains(storedFileName)) continue; // 있으면 스킵
      if (file.delete()) log.info(">>>>>> {} deleted", storedFileName);
      else log.info(">>>>>> {} failed to delete", storedFileName);
    }

    log.info(">>>>>>>>>> FileSweeper End >> {}", LocalDateTime.now());
  }
}
