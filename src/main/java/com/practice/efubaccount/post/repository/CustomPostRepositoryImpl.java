package com.practice.efubaccount.post.repository;

import com.practice.efubaccount.account.domain.QAccount;
import com.practice.efubaccount.post.domain.Post;
import com.practice.efubaccount.post.domain.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> search(String keyword, String writerNickname) {

        QPost post = QPost.post;
        QAccount account = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();

        // 작성자 닉네임 조건
        if (writerNickname != null && !writerNickname.isBlank()) {
            builder.and(post.writer.nickname.eq(writerNickname));
        }

        // 게시글 제목 또는 내용 검색
        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword))
            );
        }

        return queryFactory
                .selectFrom(post)
                .join(post.writer, account).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}