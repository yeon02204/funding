package com.funding.funding.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funding.funding.domain.user.dto.AuthDtos;
import com.funding.funding.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

// AuthController 통합 테스트
// - @SpringBootTest + @AutoConfigureMockMvc: 실제 서버 없이 HTTP 요청/응답 검증
// - @Transactional: 각 테스트 후 DB 롤백
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    private static long COUNTER = System.currentTimeMillis(); // 중복 방지용

    // ────────────────────────────────────────
    // 회원가입 테스트
    // ────────────────────────────────────────

    @Test
    void 회원가입_성공() throws Exception {
        String email    = "test" + COUNTER++ + "@example.com";
        String nickname = "tester" + COUNTER++;

        AuthDtos.RegisterReq req = new AuthDtos.RegisterReq(email, nickname, "password123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // DB에 저장되었는지 확인
        assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    void 이메일_중복_회원가입_409() throws Exception {
        String email    = "dup" + COUNTER++ + "@example.com";
        String nickname = "duper" + COUNTER++;
        AuthDtos.RegisterReq first = new AuthDtos.RegisterReq(email, nickname, "password123!");

        // 첫 번째 가입
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        // 같은 이메일로 두 번째 가입 시도
        AuthDtos.RegisterReq second = new AuthDtos.RegisterReq(email, "othernick" + COUNTER++, "password123!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    void 닉네임_중복_회원가입_409() throws Exception {
        String nickname = "samenick" + COUNTER++;
        AuthDtos.RegisterReq first  = new AuthDtos.RegisterReq("first" + COUNTER++ + "@example.com", nickname, "password123!");
        AuthDtos.RegisterReq second = new AuthDtos.RegisterReq("second" + COUNTER++ + "@example.com", nickname, "password123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict()); // 409
    }

    // ────────────────────────────────────────
    // 로그인 테스트
    // ────────────────────────────────────────

    @Test
    void 로그인_성공_토큰_반환() throws Exception {
        // 먼저 가입
        String email    = "login" + COUNTER++ + "@example.com";
        String nickname = "loginuser" + COUNTER++;
        String password = "mypassword123";
        AuthDtos.RegisterReq reg = new AuthDtos.RegisterReq(email, nickname, password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        // 로그인
        AuthDtos.LoginReq login = new AuthDtos.LoginReq(email, password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists()); // 토큰 있는지 확인
    }

    @Test
    void 존재하지_않는_이메일_로그인_401() throws Exception {
        AuthDtos.LoginReq req = new AuthDtos.LoginReq("nobody@example.com", "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void 비밀번호_틀리면_401() throws Exception {
        // 가입
        String email = "wrongpw" + COUNTER++ + "@example.com";
        AuthDtos.RegisterReq reg = new AuthDtos.RegisterReq(email, "nick" + COUNTER++, "correctpw");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk());

        // 틀린 비밀번호로 로그인
        AuthDtos.LoginReq login = new AuthDtos.LoginReq(email, "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized()); // 401
    }
}