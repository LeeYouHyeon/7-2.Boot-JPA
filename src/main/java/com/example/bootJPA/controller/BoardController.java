package com.example.bootJPA.controller;

import com.example.bootJPA.dto.BoardDTO;
import com.example.bootJPA.handler.PagingHandler;
import com.example.bootJPA.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board/*")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/register")
    public void register() {}

    @PostMapping("/register")
    public String register(BoardDTO boardDTO) {
        // MyBatis : insert, update, delete => return 1 row (성공한 row의 갯수)
        // JPA : insert, update, delete => return ID
        Long bno = boardService.insert(boardDTO);
        log.info(">>>> insert id >> {}", bno);
        return "index";
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
        model.addAttribute("boardDTO", boardService.getOne(bno));
    }

    @ResponseBody
    @GetMapping("/delete")
    public String delete(@RequestParam("bno") Long bno) {
        log.info(">>>> delete bno >> {}", bno);
        boardService.delete(bno);
        return "1";
    }

    @PostMapping("/update")
    public String update(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            boardService.update(boardDTO);
            return "redirect:/board/detail";
        } catch (EntityNotFoundException e) {
            return "redirect:/board/list";
        }
    }
}
