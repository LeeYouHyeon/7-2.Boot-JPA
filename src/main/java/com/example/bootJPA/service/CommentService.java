package com.example.bootJPA.service;

import com.example.bootJPA.dto.CommentDTO;
import com.example.bootJPA.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

public interface CommentService {
    default Comment convertDtoToEntity(CommentDTO commentDTO) {
        return Comment.builder()
                .cno(commentDTO.getCno())
                .bno(commentDTO.getBno())
                .writer(commentDTO.getWriter())
                .content(commentDTO.getContent())
                .parent(commentDTO.getParent())
                .replyCount(commentDTO.getReplyCount())
                .build();
    }

    default CommentDTO convertEntityToDto(Comment comment) {
        return CommentDTO.builder()
                .cno(comment.getCno())
                .bno(comment.getBno())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .regDate(comment.getRegDate())
                .modDate(comment.getModDate())
                .parent(comment.getParent())
                .replyCount(comment.getReplyCount())
                .build();
    }

    long post(CommentDTO commentDTO);

    Page<CommentDTO> getPageList(Long bno, int page);

    void delete(Long cno);

    void update(CommentDTO commentDTO);
}
