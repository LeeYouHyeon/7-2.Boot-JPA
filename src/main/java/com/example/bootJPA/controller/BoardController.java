package com.example.bootJPA.controller;

import com.example.bootJPA.dto.BoardDTO;
import com.example.bootJPA.dto.BoardFileDTO;
import com.example.bootJPA.dto.FileDTO;
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

    @GetMapping("/register")
    public void register() {
    }

    @PostMapping("/register")
    public String register(BoardDTO boardDTO,
                           @RequestParam(name = "files", required = false)
                           MultipartFile[] files) {
        List<FileDTO> fileList = null;
        if (files != null && files[0].getSize() > 0) {
            // 파일 핸들러 작업
            FileHandler fileHandler = new FileHandler();
            fileList = fileHandler.uploadFiles(files);
        }

        // MyBatis : insert, update, delete => return 1 row (성공한 row의 갯수)
        // JPA : insert, update, delete => return ID
        Long bno = boardService.insert(new BoardFileDTO(boardDTO, fileList));
        log.info(">>>> insert id >> {}", bno);
        return "redirect:/board/detail?bno=" + bno;
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

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("bno") Long bno) {
        model.addAttribute("boardFileDTO", boardService.detail(bno));
    }

    @ResponseBody
    @GetMapping("/delete")
    public String delete(@RequestParam("bno") Long bno) {
        log.info(">>>> delete bno >> {}", bno);
        boardService.delete(bno);
        return "1";
    }

    @PostMapping("/update")
    public String update(BoardDTO boardDTO,
                         @RequestParam(name = "files", required = false)
                         MultipartFile[] files,
                         RedirectAttributes redirectAttributes) {
        try {
            List<FileDTO> fileList = null;
            if (files != null && files[0].getSize() > 0)
                // 파일 핸들러 작업
                fileList = new FileHandler().uploadFiles(files);

            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            boardService.update(new BoardFileDTO(boardDTO, fileList));
            return "redirect:/board/detail";
        } catch (EntityNotFoundException e) {
            return "redirect:/board/list";
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

    @GetMapping("/toast")
    public void toast() {}

    @PostMapping("/toast")
    @ResponseBody
    public String toast(@RequestBody BoardDTO boardDTO) {
        try {
            InnerImageHandler handler = new InnerImageHandler();
            Document doc = Jsoup.parseBodyFragment(boardDTO.getContent());
            for (Element element : doc.select("img")) {
                log.info(">>>> element >> {}", element.toString());
                if (!element.hasAttr("src")) continue;

                String src = element.attr("src");
                if (src.startsWith("data:image")) handler.saveBase64(element);
            }
            log.info(">>>> parse result >> {}", doc.body().html());

            return "1";
        } catch (Exception e) {
            return "0";
        }
    }
}
