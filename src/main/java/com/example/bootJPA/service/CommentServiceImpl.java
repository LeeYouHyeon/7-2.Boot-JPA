package com.example.bootJPA.service;

import com.example.bootJPA.dto.CommentDTO;
import com.example.bootJPA.entity.Board;
import com.example.bootJPA.entity.Comment;
import com.example.bootJPA.repository.BoardRepository;
import com.example.bootJPA.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;

  @Transactional
  @Override
  public Long post(CommentDTO commentDTO) {
    try {
      Board board = boardRepository.findById(commentDTO.getBno()).orElseThrow(Exception::new);
      board.setCmtCount(board.getCmtCount() + 1);
      commentDTO.setReplyCount(0L);
      if (commentDTO.getParent() != null) {
        Comment parent = commentRepository.findById(commentDTO.getParent())
            .orElseThrow(() -> new Exception("ignore"));
        parent.setReplyCount(parent.getReplyCount() + 1);
      }
      return commentRepository
          .save(convertDtoToEntity(commentDTO))
          .getCno();
    } catch (Exception ignored) {
      return 0L;
    }
  }

  @Override
  public Page<CommentDTO> getPageList(Long bno, int page) {
    Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("cno").descending());
    return commentRepository.findByBno(bno, pageable)
        .map(this::convertEntityToDto);
  }

  @Transactional
  @Override
  public void delete(Long cno) {
    Comment comment = commentRepository.findById(cno).orElse(null);
    if (comment == null) return;

    commentRepository.delete(comment);
    List<Comment> children = new ArrayList<>();

    // comment가 부모인가 자식인가에 따라 할 일이 달라짐
    // 부모 : 댓글 삭제, 자신의 자식들 삭제, 글의 댓글수 감소
    // 자식 : 댓글 삭제, 부모의 childCount 감소, 글의 댓글수 1 감소
    try {
      if (comment.getParent() == null) {
        children = commentRepository.findByParent(cno);
        children.forEach(commentRepository::delete);
      } else {
        Comment parent = commentRepository.findById(comment.getParent())
            .orElseThrow();
        parent.setReplyCount(parent.getReplyCount() - 1);
      }
    } catch (Exception ignore) {
    }

    try {
      Board board = boardRepository.findById(comment.getBno())
          .orElseThrow(Exception::new);
      board.setCmtCount(board.getCmtCount() - 1 - children.size());

      children.forEach(commentRepository::delete);
    } catch (Exception ignored) {
    }
  }

  @Transactional
  @Override
  public void update(CommentDTO commentDTO) {
    Comment entity = commentRepository
        .findById(commentDTO.getCno())
        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글"));
    entity.setContent(commentDTO.getContent());
  }

  @Override
  public List<CommentDTO> getReply(Long cno) {
    return commentRepository.findByParent(cno)
        .stream().map(this::convertEntityToDto)
        .toList();
  }
}
