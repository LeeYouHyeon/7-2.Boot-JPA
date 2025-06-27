package com.example.bootJPA.repository;

import com.example.bootJPA.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.bootJPA.entity.QUser.user;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    public UserCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<User> searchUser(String type, String keyword, Pageable pageable) {
        BooleanExpression condition = null;

        if (type != null && keyword != null) {
            char[] typeArr = type.toCharArray();
            for (char t : typeArr) {
                condition = switch (t) {
                    case 'e' -> condition == null ?
                            user.email.containsIgnoreCase(keyword)
                            : condition.or(user.email.containsIgnoreCase(keyword));
                    case 'n' -> condition == null ?
                            user.nickName.containsIgnoreCase(keyword)
                            : condition.or(user.nickName.containsIgnoreCase(keyword));
                    default -> condition;
                };
            }
        }

        List<User> result = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(user.regDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory
                .selectFrom(user)
                .where(condition)
                .fetch().size();

        return new PageImpl<>(result, pageable, totalCount);
    }
}
