package com.practice.efubaccount.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BioUpdateRequestDto {
    @NotBlank
    private String bio;

}
