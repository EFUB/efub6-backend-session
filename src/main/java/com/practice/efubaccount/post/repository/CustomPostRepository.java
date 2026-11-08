package com.practice.efubaccount.post.repository;

import com.practice.efubaccount.post.domain.Post;
import java.util.List;

public interface CustomPostRepository {

    List<Post> search(String keyword, String writerNickname);
}