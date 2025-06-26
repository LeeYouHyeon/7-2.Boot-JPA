package com.example.bootJPA.handler;

import com.example.bootJPA.dto.FileDTO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileHandler {
    /* 파일을 폴더에 저장하고, fileList를 리턴하는 클래스 */

    // new File에서 부모로 사용되기 때문에 끝에 구분자가 필요없음
    private final String UP_DIR = "D:\\web_0226_lyh\\_myProject\\_java\\_fileUpload";

    public List<FileDTO> uploadFiles(MultipartFile[] files) {
        List<FileDTO> fileList = new ArrayList<>();
        // 날짜 형태로 폴더를 구성
        LocalDate date = LocalDate.now(); //2025-06-19
        String today = date.toString().replace("-", File.separator);

        File folders = new File(UP_DIR, today); // folder
        if (!folders.exists()) folders.mkdirs();

        // FileDTO 생성 : 각자 파일마다 생성
        for (MultipartFile file : files) {
            // file : name, size
            log.info(">>> file type >> {}", file.getContentType());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSaveDir(today);
            fileDTO.setFileSize(file.getSize());
            String fileDTOName = file.getOriginalFilename();
            fileDTO.setFileName(fileDTOName);

            String uuid = UUID.randomUUID().toString();
            fileDTO.setUuid(uuid);
            int type = file.getContentType().startsWith("image") ? 1 : 0;
            fileDTO.setFileType(type);

            log.info(">>> FileDTO >> {}", fileDTO);

            // ------------fileSave
            String fileName = uuid + "_" + fileDTOName;
            String fileThName = uuid + "_th_" + fileDTOName;

            // 실 저장 파일
            File storeFile = new File(folders, fileName);
            try {
                file.transferTo(storeFile);
                if (type == 1) {
                    File thumbnail = new File(folders, fileThName);
                    Thumbnails.of(storeFile)
                            .size(100, 100).toFile(thumbnail);
                }
            } catch (Exception e) {
                log.info("file save error");
                e.printStackTrace();
            }
            fileList.add(fileDTO);
        }

        return fileList;
    }
}