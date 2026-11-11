package com.practice.efubaccount.post;

import com.practice.efubaccount.account.domain.Account;
import com.practice.efubaccount.account.repository.AccountRepository;
import com.practice.efubaccount.post.domain.Post;
import com.practice.efubaccount.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QueryDslTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        account1 = accountRepository.save(Account.builder()
                .email("week15-ewha@example.com")
                .password("pass1")
                .nickname("week15_ewha")
                .build());

        account2 = accountRepository.save(Account.builder()
                .email("week15-fubi@example.com")
                .password("pass2")
                .nickname("week15_fubi")
                .build());

        postRepository.save(Post.builder()
                .title("QueryDSL Practice")
                .content("Dynamic Query")
                .writer(account1)
                .build());

        postRepository.save(Post.builder()
                .title("JPA Study")
                .content("Learning querydsl")
                .writer(account1)
                .build());

        postRepository.save(Post.builder()
                .title("QUERYDSL Tutorial")
                .content("EFUB")
                .writer(account2)
                .build());
    }

    @Test
    void searchByKeyword() {
        List<Post> result = postRepository.search("querydsl", null);

        assertThat(result).hasSize(3);
    }

    @Test
    void searchByWriterNickname() {
        List<Post> result = postRepository.search(null, "week15_ewha");

        assertThat(result).hasSize(2);
    }

    @Test
    void searchByKeywordAndWriter() {
        List<Post> result = postRepository.search(
                "querydsl", "week15_fubi"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle())
                .isEqualTo("QUERYDSL Tutorial");
    }
}