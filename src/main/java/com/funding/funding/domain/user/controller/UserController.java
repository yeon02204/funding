package com.funding.funding.domain.user.controller;

import com.funding.funding.global.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ApiResponse<Object> me(Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ApiResponse.fail("UNAUTHORIZED");
        }

        Object principal = auth.getPrincipal();

        // ✅ JwtAuthenticationFilter에서 principal을 Long(userId)로 넣었을 때
        if (principal instanceof Long userId) {
            return ApiResponse.ok(userId);
        }

        // ✅ 혹시 String으로 들어오는 경우(예: "1")
        if (principal instanceof String s) {
            try {
                return ApiResponse.ok(Long.valueOf(s));
            } catch (Exception e) {
                return ApiResponse.ok(principal);
            }
        }

        // ✅ 그 외(UserDetails 등)면 일단 그대로 보여줘서 원인 파악 가능하게
        return ApiResponse.ok(principal);
    }
}