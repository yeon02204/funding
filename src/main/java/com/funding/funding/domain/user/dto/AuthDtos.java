package com.funding.funding.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record RegisterReq(
            @Email @NotBlank String email,
            @NotBlank String nickname,
            @NotBlank String password
    ) {}

    public record LoginReq(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record TokenRes(String accessToken) {}
}