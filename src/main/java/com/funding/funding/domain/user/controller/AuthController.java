package com.funding.funding.domain.user.controller;

import com.funding.funding.domain.user.dto.AuthDtos;
import com.funding.funding.domain.user.service.auth.AuthService;
import com.funding.funding.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody AuthDtos.RegisterReq req) {
        authService.register(req);
        return ApiResponse.ok("회원가입 완료", null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.TokenRes> login(@Valid @RequestBody AuthDtos.LoginReq req) {
        return ApiResponse.ok(authService.login(req));
    }
}