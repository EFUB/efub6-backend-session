package com.practice.efubaccount.follow.domain;

import com.practice.efubaccount.account.domain.Account;
import com.practice.efubaccount.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Account <-> Account 관계에서 사이에 follow 추가
    //팔로우 하는 사람
    @ManyToOne(fetch = FetchType.LAZY) //A-나 B-나 C-나 의 관계라서 many to one
    @JoinColumn(name = "follwer_id", nullable = false, updatable = false)
    private Account follower;

    //팔로우 당하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false, updatable = false)
    private Account followee;

    @Builder
    public Follow(Account follower, Account followee){
        this.follower = follower;
        this.followee = followee;
    }
}
