package com.practice.efubaccount.post.dto.response;

import com.practice.efubaccount.post.domain.Post;

public record PostSearchResponseDto(
        Long postId,
        String title,
        String content,
        String writerNickname,
        Long viewCount
) {
    public PostSearchResponseDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getNickname(),
                post.getViewCount()
        );
    }
}