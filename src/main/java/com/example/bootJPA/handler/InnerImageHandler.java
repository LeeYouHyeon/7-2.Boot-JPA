package com.example.bootJPA.handler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public class InnerImageHandler {
    private final String filePath;

    // 모듈화를 위한 생성자
    public InnerImageHandler() {
        this("D:\\web_0226_lyh\\_myProject\\_java\\_fileUpload\\innerImage\\");
    }
    public InnerImageHandler(String path) {
        this.filePath = path;
    }

    public void saveBase64(Element image) throws Exception {
        try {
            String src = image.attr("src") // data:image/(확장자);base64,(base64코드)
                    , alt = image.attr("alt"); // 고양이.jpg
            String base64 = src.substring(src.indexOf(",") + 1);
            String extension = extension(base64, alt);

            byte[] imageBytes = Base64.getDecoder().decode(base64);

            String fileDir = LocalDate.now().toString().replace("-", File.separator);
            File saveDir = new File(filePath + fileDir);
            if (!saveDir.exists() && !saveDir.mkdirs()) throw new Exception("서버 오류입니다. 관리자에게 문의하세요.");

            String fileNameWithUuid = UUID.randomUUID() + "_" + alt;
            File outputFile = new File(saveDir, fileNameWithUuid);

            BufferedImage saved = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
            if (saved == null) throw new Exception(alt + " 파일을 분석하는 데에 실패했습니다. 이미지 파일이 맞는지 다시 확인해주세요.");
            log.info(">>> step 4");
            log.info(">>> saved {}", saved);
            log.info(">>> extension {}", extension);
            log.info(">>> outputFile {}", outputFile);
            ImageIO.write(saved, extension, outputFile);

            image.attr("src", "/upload/innerImage/" + fileDir.replace(File.separator, "/") + "/" + fileNameWithUuid);
        } catch (IOException e) {
            throw new Exception("이미지를 업로드하는 도중 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
    }

    public String extension(String base64, String alt) throws Exception {
        String header = base64.substring(0, 20);
        if (header.startsWith("/9j/")) return "jpeg";
        if (header.startsWith("iVBORw0KGgo")) return "png";
        if (header.startsWith("R0lGOD")) return "gif";
        if (header.startsWith("UklGR")) return "webp";
        if (header.startsWith("Qk")) return "bmp";
        if (header.startsWith("PD94bWwg")) return "svg";

        throw new Exception(alt + " 파일을 분석하는 데에 실패했습니다. 이미지 파일이 맞는지 다시 확인해주세요.");
    }
}
