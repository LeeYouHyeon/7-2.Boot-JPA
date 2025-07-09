package com.example.bootJPA.repository;

import com.example.bootJPA.entity.Board;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.bootJPA.entity.QBoard.board;

public class BoardCustomRepositoryImpl implements BoardCustomRepository {

  private final JPAQueryFactory queryFactory;

  public BoardCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  // 실제 구현
  @Override
  public Page<Board> searchBoard(String type, String keyword, Pageable pageable) {
    /* BooleanExpression : 일반적으로 동적 쿼리를 작성할 때 사용하는 객체 */
    /* select * from board
     * where isDel = 'N' and title like concat('%', keyword, '%')
     * where isDel = 'N' and (title like concat('%', keyword, '%') or writer like ...)
     *
     * BooleanExpression condition = board.isDel.equals("N");
     * condition = condition.and(board.title.containsIgnoreCase(keyword));
     * */
    BooleanExpression condition = null;

    // 동적 검색 조건 추가
    // 타입이 여러 개면 배열로 처리
    // enhanced switch (Java 14 이상부터 사용 가능)
    if (type != null && keyword != null) {
      char[] typeArr = type.toCharArray();
      for (char t : typeArr) {
        condition = switch (t) {
          case 't' -> condition == null ?
              board.title.containsIgnoreCase(keyword)
              : condition.or(board.title.containsIgnoreCase(keyword));
          case 'w' -> condition == null ?
              board.writer.containsIgnoreCase(keyword)
              : condition.or(board.writer.containsIgnoreCase(keyword));
          case 'c' -> condition == null ?
              board.content.containsIgnoreCase(keyword)
              : condition.or(board.content.containsIgnoreCase(keyword));
          default -> condition;
        };
      }
    }

    // 쿼리 작성 및 페이징 적용
    // where에 null이 들어가면 조건이 없는 것처럼 작동함
    List<Board> result = queryFactory
        .selectFrom(board)
        .where(condition)
        .orderBy(board.bno.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 검색된 데이터의 전체 갯수 조회
    long totalCount = queryFactory
        .selectFrom(board)
        .where(condition)
        .fetch().size();
    return new PageImpl<>(result, pageable, totalCount);
  }
}
