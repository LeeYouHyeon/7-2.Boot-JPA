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

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public long post(CommentDTO commentDTO) {
        try {
            Board board = boardRepository.findById(commentDTO.getBno()).orElseThrow(Exception::new);
            board.setCmtCount(board.getCmtCount() + 1);
            return commentRepository
                    .save(convertDtoToEntity(commentDTO))
                    .getCno();
        } catch (Exception ignored) {
            return 0;
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
        try {
            Board board = boardRepository.findById(comment.getBno())
                    .orElseThrow(Exception::new);
            board.setCmtCount(board.getCmtCount() - 1);
        } catch (Exception ignored) {}
    }

    @Transactional
    @Override
    public void update(CommentDTO commentDTO) {
        Comment entity = commentRepository
                .findById(commentDTO.getCno())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글"));
        entity.setContent(commentDTO.getContent());
    }
}
