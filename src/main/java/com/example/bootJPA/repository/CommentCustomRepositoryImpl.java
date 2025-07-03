package com.example.bootJPA.repository;

import com.example.bootJPA.entity.Comment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.bootJPA.entity.QComment.comment;

public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    public CommentCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Comment> findByBno(Long bno, Pageable pageable) {
        BooleanExpression condition = comment.parent.isNull()
                .and(comment.bno.eq(bno));

        List<Comment> result = queryFactory
                .selectFrom(comment)
                .where(condition)
                .orderBy(comment.cno.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(comment)
                .where(condition)
                .fetch().size();

        return new PageImpl<>(result, pageable, totalCount);
    }
}
