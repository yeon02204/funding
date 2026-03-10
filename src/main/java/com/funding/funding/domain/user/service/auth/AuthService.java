package com.funding.funding.domain.user.service.auth;

import com.funding.funding.domain.user.dto.AuthDtos;
import com.funding.funding.domain.user.entity.*;
import com.funding.funding.domain.user.repository.EmailVerificationTokenRepository;
import com.funding.funding.domain.user.repository.PasswordResetTokenRepository;
import com.funding.funding.domain.user.repository.UserRepository;
import com.funding.funding.domain.user.service.email.EmailService;
import com.funding.funding.global.exception.ApiException;
import com.funding.funding.global.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository,
                       EmailVerificationTokenRepository emailTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwt,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.emailService = emailService;
    }

    @Transactional
    public void register(AuthDtos.RegisterReq req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User user = new User(
                req.email(), req.nickname(),
                passwordEncoder.encode(req.password()),
                UserRole.USER, UserStatus.ACTIVE, AuthProvider.LOCAL
        );
        userRepository.save(user);

        sendVerificationCode(req.email());
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

        user.updateLastLoginAt();

        String access = jwt.createAccessToken(user.getId(), user.getRole().name());
        return new AuthDtos.TokenRes(access);
    }

    @Transactional
    public AuthDtos.TokenRes socialLogin(
            AuthProvider provider,
            String providerId,
            String email,
            String nickname,
            String profileImage
    ) {
        if (email == null || email.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "소셜 계정에서 이메일 정보를 제공하지 않았습니다.");
        }

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createSocialUser(provider, providerId, email, nickname, profileImage));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "활성 상태의 계정이 아닙니다.");
        }

        user.updateSocialInfo(nickname, profileImage, providerId);

        String access = jwt.createAccessToken(user.getId(), user.getRole().name());
        return new AuthDtos.TokenRes(access);
    }

    @Transactional
    protected User createSocialUser(
            AuthProvider provider,
            String providerId,
            String email,
            String nickname,
            String profileImage
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "이미 일반회원 또는 다른 소셜 계정으로 가입된 이메일입니다. 기존 방식으로 로그인해주세요."
            );
        }

        String finalNickname = makeUniqueNickname(nickname, provider);

        User newUser = User.createSocialUser(
                email,
                finalNickname,
                provider,
                providerId,
                profileImage
        );

        return userRepository.save(newUser);
    }

    @Transactional
    public void sendVerificationCode(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "가입되지 않은 이메일입니다.");
        }

        String code = generateSixDigitCode();
        EmailVerificationToken token = EmailVerificationToken.create(email, code, 5);
        emailTokenRepository.save(token);

        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public void verifyEmail(AuthDtos.VerifyEmailReq req) {
        EmailVerificationToken token = emailTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(req.email())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "인증 코드를 먼저 요청해주세요."));

        if (token.isUsed()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 사용된 인증 코드입니다.");
        }
        if (token.isExpired()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다. 다시 요청해주세요.");
        }
        if (!token.getCode().equals(req.code())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다.");
        }

        token.markUsed();
        userRepository.findByEmail(req.email()).ifPresent(User::verifyEmail);
    }

    public AuthDtos.FindEmailRes findEmail(AuthDtos.FindEmailReq req) {
        User user = userRepository.findByNickname(req.nickname())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 닉네임의 회원을 찾을 수 없습니다."));

        String masked = maskEmail(user.getEmail());
        emailService.sendFoundEmail(user.getEmail(), masked);

        return new AuthDtos.FindEmailRes(masked);
    }

    @Transactional
    public void requestPasswordReset(AuthDtos.PasswordResetRequestReq req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "가입되지 않은 이메일입니다."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.create(user, token, 30);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetLink(user.getEmail(), token, frontendUrl);
    }

    @Transactional
    public void resetPassword(AuthDtos.PasswordResetReq req) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(req.token())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "유효하지 않은 토큰입니다."));

        if (resetToken.isUsed()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 사용된 토큰입니다.");
        }
        if (resetToken.isExpired()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "만료된 토큰입니다. 비밀번호 찾기를 다시 시도해주세요.");
        }

        resetToken.markUsed();
        resetToken.getUser().changePassword(passwordEncoder.encode(req.newPassword()));
    }

    private String generateSixDigitCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        String masked = local.substring(0, 2) + "*".repeat(local.length() - 2);
        return masked + domain;
    }

    private String makeUniqueNickname(String nickname, AuthProvider provider) {
        String base = (nickname == null || nickname.isBlank())
                ? provider.name().toLowerCase() + "_user"
                : nickname.trim();

        String candidate = base;
        int suffix = 1;

        while (userRepository.existsByNickname(candidate)) {
            candidate = base + suffix;
            suffix++;
        }

        return candidate;
    }
}