package com.example.bootJPA.repository;

/* JpaRepository<테이블명, id> */

import com.example.bootJPA.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository {

    /* id가 아닌 다른 조건으로 값을 검색할 때 사용 */
//    @Query("select b from Board b where b.writer = ?")
//    List<Board> findByWriter(String writer);

}
