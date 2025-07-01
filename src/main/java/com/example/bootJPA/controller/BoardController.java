package com.example.bootJPA.controller;

import com.example.bootJPA.dto.BoardDTO;
import com.example.bootJPA.dto.BoardFileDTO;
import com.example.bootJPA.dto.FileDTO;
import com.example.bootJPA.entity.Board;
import com.example.bootJPA.handler.FileHandler;
import com.example.bootJPA.handler.InnerImageHandler;
import com.example.bootJPA.handler.PagingHandler;
import com.example.bootJPA.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
public class BoardController {
    private final BoardService boardService;

    // 에디터 내부의 이미지 처리
    private String parseContent(String content) throws Exception {
        InnerImageHandler handler = new InnerImageHandler();
        Document doc = Jsoup.parseBodyFragment(content);
        doc.outputSettings().prettyPrint(false);

        for (Element element : doc.select("img")) {
            log.info(">>>> element >> {}", element.toString());
            if (!element.hasAttr("src")) continue;

            element.addClass("mw-100");
            String src = element.attr("src");
            if (src.startsWith("data:image")) handler.saveBase64(element);
        }
        return doc.body().html();
    }

    @GetMapping("/register")
    public void register() {}

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestPart("bdto") BoardDTO boardDTO,
                           @RequestPart(name = "files[]", required = false)
                           MultipartFile[] files) {
        log.info(">>>> board register in");
        try {
            boardDTO.setContent(parseContent(boardDTO.getContent()));
            log.info(">>> HTML parse successful");

            // 첨부파일 처리
            List<FileDTO> fileList = null;
            if (files != null && files[0].getSize() > 0) {
                // 파일 핸들러 작업
                FileHandler fileHandler = new FileHandler();
                fileList = fileHandler.uploadFiles(files);
            }
            log.info(">>>> files handling successful");

            Long bno = boardService.insert(new BoardFileDTO(boardDTO, fileList));
            log.info(">>>> insert id >> {}", bno);
            return String.valueOf(bno);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Paging이 없는 일반 리스트
    /*
    @GetMapping("/list")
    public void list(Model model) {
        model.addAttribute("list", boardService.getList());
    }*/

    @GetMapping("/list")
    public void list(Model model,
                     @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                     @RequestParam(name = "type", required = false) String type,
                     @RequestParam(name = "keyword", required = false) String keyword) {
        /* select * from board order by bno desc limit 0, 10 */
        /* limit 시작번지, 갯수 => 시작 번지는 0부터 */
        Page<BoardDTO> list = boardService.getList(pageNo, 10, type, keyword);
        model.addAttribute("ph", new PagingHandler<>(list, type, keyword));
    }

    @GetMapping("/modify")
    public void modify(Model model, @RequestParam("bno") Long bno) {
        model.addAttribute("boardFileDTO", boardService.detail(bno, false));
    }

    @GetMapping("/detail")
    public void detail(Model model,
                       @RequestParam("bno") Long bno,
                       @RequestParam(name = "isReal", defaultValue = "true") boolean isReal) {
        log.info(">>>> isReal >> {}", isReal);
        model.addAttribute("boardFileDTO", boardService.detail(bno, isReal));
    }

    @ResponseBody
    @GetMapping("/delete/{bno}")
    public String delete(@PathVariable("bno") Long bno) {
        log.info(">>>> delete bno >> {}", bno);
        boardService.delete(bno);
        return "1";
    }

    @ResponseBody
    @PostMapping("/update")
    public String update(@RequestPart("bdto") BoardDTO boardDTO,
                         @RequestPart(name = "files[]", required = false) MultipartFile[] files,
                         RedirectAttributes redirectAttributes) {
        log.info(">>>> update in");
        try {
            boardDTO.setContent(parseContent(boardDTO.getContent()));
            log.info(">>> content parsed");

            List<FileDTO> fileList = null;
            if (files != null && files[0].getSize() > 0)
                // 파일 핸들러 작업
                fileList = new FileHandler().uploadFiles(files);
            log.info(">>> file handled");

            boardService.update(new BoardFileDTO(boardDTO, fileList));

            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            redirectAttributes.addAttribute("isReal", false);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @DeleteMapping("/file/{uuid}")
    @ResponseBody
    public String fileRemove(@PathVariable("uuid") String uuid) {
        try {
            boardService.fileRemove(uuid);
            return "1";
        } catch (EntityNotFoundException e) {
            return "0";
        }
    }

}
