package com.funding.funding.domain.user.service.auth;

import com.funding.funding.domain.user.dto.AuthDtos;
import com.funding.funding.domain.user.entity.AuthProvider;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.user.entity.UserRole;
import com.funding.funding.domain.user.entity.UserStatus;
import com.funding.funding.domain.user.repository.UserRepository;
import com.funding.funding.global.exception.ApiException;
import com.funding.funding.global.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwt) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    public void register(AuthDtos.RegisterReq req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User user = new User(
                req.email(),
                req.nickname(),
                passwordEncoder.encode(req.password()),
                UserRole.USER,
                UserStatus.ACTIVE,
                AuthProvider.LOCAL
        );
        userRepository.save(user);
    }

    public AuthDtos.TokenRes login(AuthDtos.LoginReq req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (user.getPassword() == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "활성 상태의 계정이 아닙니다.");
        }

        String access = jwt.createAccessToken(user.getId(), user.getRole().name());
        return new AuthDtos.TokenRes(access);
    }
}