package com.example.bootJPA.handler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public class InnerImageHandler {
    private final String filePath = "D:\\web_0226_lyh\\_myProject\\_java\\_fileUpload\\";

    public void saveBase64(Element image) throws Exception {
        String src = image.attr("src") // data:image/(확장자);base64,(base64코드)
                , alt = image.attr("alt") // 고양이.jpg
                , extension = alt.substring(alt.lastIndexOf(".") + 1);
        String base64 = src.substring(src.indexOf(",") + 1);

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        String fileDir = LocalDate.now().toString().replace("-", "/");
        File saveDir = new File(filePath + fileDir);
        if (!saveDir.exists() && !saveDir.mkdirs()) throw new Exception();

        String fileNameWithUuid = UUID.randomUUID() + "_" + alt;
        File outputFile = new File(saveDir, fileNameWithUuid);

        BufferedImage saved = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        ImageIO.write(saved, extension, outputFile);

        image.attr("src", "/upload/" + fileDir + "/" + fileNameWithUuid);
    }
}
