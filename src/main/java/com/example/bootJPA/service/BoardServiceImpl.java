package com.example.bootJPA.service;

import com.example.bootJPA.dto.BoardDTO;
import com.example.bootJPA.dto.BoardFileDTO;
import com.example.bootJPA.dto.FileDTO;
import com.example.bootJPA.entity.Board;
import com.example.bootJPA.entity.File;
import com.example.bootJPA.repository.BoardRepository;
import com.example.bootJPA.repository.FileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;

    @Transactional
    @Override
    public Long insert(BoardFileDTO boardFileDTO) {
        // 저장 객체는 Board
        // save() : 저장
        // entity 객체를 파라미터로 전송
        long bno = boardRepository.save(convertDtoToEntity(boardFileDTO.getBoardDTO())).getBno();

        List<FileDTO> files = boardFileDTO.getFileList();
        if (files != null) for (FileDTO fileDTO : files) {
            fileDTO.setBno(bno);
            fileRepository.save(convertDtoToEntity(fileDTO));
        }

        return bno;
    }

//    @Override
//    public List<BoardDTO> getList() {
//        /* controller로 List<BoardDTO>를 보냄
//        * DB 에서 가져오는 리턴은 List<Board>. 이를 List<BoardDTO>로 변환해야 함
//        * select * from board order by bno desc
//        * findAll : 전체 리턴
//        * Sort.by(Sort.Direction.DESC, "정렬 기준 칼럼")
//        * findById : id가 일치하는 값
//        *  */
//
//        /*
//        List<Board> boardList = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "bno"));
//        List<BoardDTO> boardDTOList = new ArrayList<>(boardList.size());
//        for (Board board : boardList) boardDTOList.add(convertEntityToDto(board));
//        return boardDTOList;
//        */
//    }

    @Override
    public Page<BoardDTO> getList(int pageNo, int pageSize, String type, String keyword) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("bno").descending());
        return boardRepository.searchBoard(type, keyword, pageable)
                .map(this::convertEntityToDto);
    }

    @Transactional
    @Override
    public BoardFileDTO detail(Long bno, boolean isReal) {
        /*
        Optional<T> : NullPointerException을 막는 용도
        (Optional 객체).isEmpty() : null 이면 true / 아니면 false
        (Optional 객체).isPresent() : isEmpty()와 반대
        (Optional 객체).get() : 언박싱
         */
        Board board = boardRepository.findById(bno).orElse(null);
        if (board == null) return null;

        if (isReal) board.setReadCount(board.getReadCount() + 1);

        BoardDTO boardDTO = this.convertEntityToDto(board);
        return new BoardFileDTO(boardDTO,
                fileRepository.findByBno(boardDTO.getBno())
                        .stream()
                        .map(this::convertEntityToDto)
                        .toList());
    }

    @Override
    public void delete(Long bno) {
        boardRepository.deleteById(bno);
    }

    /* save : boardDTO에 id가 없으면 insert, 있으면 update로 처리
     * id가 DB에 없으면 DB 에러가 생김. EntityNotFoundException => 정보 유실의 위험이 커짐
     * 없는 정보에 대한 데이터가 사라짐(null)
     * 변동 감지 (Dirty Checking) 미작동 가능성이 커짐
     */
    /* 해결 방법
     * 1) findById(bno)를 통해 먼저 조회 => 영속상태를 만든 후 수정 => save
     * 2) @Transactional : Dirty Checking 으로 업데이트 가능 / save() 없이도 가능
     *   UPDATE만 가능
     * */
    /* Dirty Checking
     * 객체가 영속성 컨텍스트에 올라가있을 때 해당 객체의 필드가 변경되면,
     * 트랜잭션이 종료되기 전에 JPA 가 변경한 부분만 자동 감지하여 update 쿼리를 실행
     * save() 없이(명시적으로 호출하지 않아도) 수정된 필드를 DB에 자동 반영함
     * */
//    @Override
//    public void update(BoardDTO boardDTO) {
//        // 1. 기존 객체를 가져와서 수정한 후 다시 저장
//        Optional<Board> optional = boardRepository.findById(boardDTO.getBno());
//        if(optional.isEmpty()) return;
//
//        Board entity = optional.get();
//        entity.setTitle(boardDTO.getTitle());
//        entity.setContent(boardDTO.getContent());
//        boardRepository.save(entity);
//    }

    @Transactional
    @Override
    public void update(BoardFileDTO boardFileDTO) {
        log.info(">>>> board service update in");
        // 2. Dirty Checking
        BoardDTO boardDTO = boardFileDTO.getBoardDTO();

        Board entity = boardRepository
                .findById(boardDTO.getBno())
                .orElse(null);
        if (entity == null) return;

        log.info(">> entity loaded");
        entity.setTitle(boardDTO.getTitle());
        entity.setContent(boardDTO.getContent());
        log.info(">> dto set ");

        List<FileDTO> files = boardFileDTO.getFileList();
        if (files != null) for (FileDTO fileDTO : files) {
            fileDTO.setBno(boardDTO.getBno());
            fileRepository.save(convertDtoToEntity(fileDTO));
        }
        log.info(">> file saved");
    }

    @Transactional
    @Override
    public void fileRemove(String uuid) throws EntityNotFoundException {
        File file = fileRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("없는 파일"));

        fileRepository.delete(file);
    }
}
