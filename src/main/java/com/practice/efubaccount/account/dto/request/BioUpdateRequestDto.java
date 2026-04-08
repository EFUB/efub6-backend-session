package com.practice.efubaccount.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BioUpdateRequestDto {
    @NotBlank
    private String bio;

}
