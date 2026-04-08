package com.funding.funding.domain.user.oauth;

import com.funding.funding.domain.user.entity.AuthProvider;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id == null ? null : id.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        Object email = kakaoAccount.get("email");
        return email == null ? null : email.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getNickname() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) return null;
        Object nickname = properties.get("nickname");
        return nickname == null ? null : nickname.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getProfileImage() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) return null;
        Object profileImage = properties.get("profile_image");
        return profileImage == null ? null : profileImage.toString();
    }
}