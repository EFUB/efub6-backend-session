package com.practice.efubaccount.comment.service;

import com.practice.efubaccount.account.dto.response.AccountCommentResponse;
import com.practice.efubaccount.comment.dto.request.CommentRequest;
import com.practice.efubaccount.post.dto.response.PostCommentResponse;
import com.practice.efubaccount.account.domain.Account;
import com.practice.efubaccount.account.service.AccountService;
import com.practice.efubaccount.comment.domain.Comment;
import com.practice.efubaccount.comment.repository.CommentRepository;
import com.practice.efubaccount.post.domain.Post;
import com.practice.efubaccount.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service  //이것 덕분에 Spring이 Bean을 인식할 수 있음!!
@RequiredArgsConstructor //final 만 모아서 생성자를 만들어주는 어노테이션 - 생성자 주입
public class CommentService {

    private final AccountService accountService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    // 댓글 생성
    @Transactional
    public Long createComment(Long postId, CommentRequest request){
        Long accountId = request.getAccountId();
        Account writer = accountService.findByAccountId(accountId);
        Post post = postService.findByPostId(postId);
        Comment newComment = request.toEntity(writer, post);
        commentRepository.save(newComment);

        return newComment.getId();
    }


    // postId 로 댓글 목록 조회
    @Transactional(readOnly = true)
    public PostCommentResponse getPostCommentList(Long postId){
        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAt(postId);
        return PostCommentResponse.of(postId, commentList);
    }

    // accountId 로 댓글 목록 조회
    @Transactional(readOnly = true)
    public AccountCommentResponse getAccountCommentList(Long accountId){
        Account account = accountService.findByAccountId(accountId);
        List<Comment> commentList = commentRepository.findAllByWriterAccountIdOrderByCreatedAtDesc(accountId);
        return AccountCommentResponse.of(account, commentList);
    }

}
