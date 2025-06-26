package com.example.bootJPA.service;

import com.example.bootJPA.dto.BoardDTO;
import com.example.bootJPA.dto.BoardFileDTO;
import com.example.bootJPA.dto.FileDTO;
import com.example.bootJPA.entity.Board;
import com.example.bootJPA.entity.File;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

public interface BoardService {

    // 추상 메서드만 가능한 인터페이스
    // default : 메서드에 기본값을 주는 예약어. default 접근제어자와는 아무런 관련이 없다.

    Long insert(BoardFileDTO boardFileDTO);

    /* BoardDTO => Board 객체로 변환
     * BoardDTO(class) : bno, title, writer, content, regDate, modDate
     * board(table) : bno, title, writer, content
     * 화면에서 가져온 BoardDTO 객체를 저장을 위한 Board 테이블 객체로 변환
     * */
    default Board convertDtoToEntity(BoardDTO boardDTO) {
        return Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .writer(boardDTO.getWriter())
                .content(boardDTO.getContent())
                .build();
    }

    /* Board 객체 => BoardDTO 객체로 변환
     * 위와 반대로 테이블에서 가져온 객체를 화면에 뿌리기 위한 객체로 변환
     * */
    default BoardDTO convertEntityToDto(Board board) {
        return BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();
    }

    default File convertDtoToEntity(FileDTO fileDTO) {
        return File.builder()
                .uuid(fileDTO.getUuid())
                .saveDir((fileDTO.getSaveDir()))
                .fileName(fileDTO.getFileName())
                .fileType(fileDTO.getFileType())
                .bno(fileDTO.getBno())
                .fileSize(fileDTO.getFileSize())
                .build();
    }

    default FileDTO convertEntityToDto(File file) {
        return FileDTO.builder()
                .uuid(file.getUuid())
                .saveDir(file.getSaveDir())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .bno(file.getBno())
                .fileSize(file.getFileSize())
                .regDate(file.getRegDate())
                .modDate(file.getModDate())
                .build();
    }

    Page<BoardDTO> getList(int pageNo, int pageSize, String type, String keyword);

    BoardFileDTO detail(Long bno);

    void delete(Long bno);

    void update(BoardFileDTO boardFileDTO);

    void fileRemove(String uuid) throws EntityNotFoundException;
}
