package com.practice.efubaccount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto { //에러 메세지 응답 DTO
    private String message;
    private int status;
}