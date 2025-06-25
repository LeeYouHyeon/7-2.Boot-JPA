package com.example.bootJPA.controller;

import com.example.bootJPA.dto.CommentDTO;
import com.example.bootJPA.handler.PagingHandler;
import com.example.bootJPA.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment/*")
public class CommentController {
    private final CommentService commentService;

    @ResponseBody
    @PostMapping("/post")
    public String post(@RequestBody CommentDTO commentDTO) {
        return String.valueOf(commentService.post(commentDTO));
    }

    @ResponseBody
    @GetMapping("/list/{bno}/{page}")
    public PagingHandler<CommentDTO> list(@PathVariable("bno") Long bno, @PathVariable("page") int page) {
//        return commentService.getList(bno);
        return new PagingHandler<>(commentService.getPageList(bno, page));
    }

    @ResponseBody
    @DeleteMapping("/delete/{cno}")
    public String delete(@PathVariable("cno") Long cno) {
        commentService.delete(cno);
        return "1";
    }

    @ResponseBody
    @PutMapping("/update")
    public String update(@RequestBody CommentDTO commentDTO) {
        try {
            commentService.update(commentDTO);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }
}
