package com.funding.funding.domain.user.oauth;

import com.funding.funding.domain.user.entity.AuthProvider;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.NAVER;
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id == null ? null : id.toString();
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email == null ? null : email.toString();
    }

    @Override
    public String getNickname() {
        Object nickname = attributes.get("nickname");
        if (nickname != null) return nickname.toString();

        Object name = attributes.get("name");
        return name == null ? null : name.toString();
    }

    @Override
    public String getProfileImage() {
        Object profileImage = attributes.get("profile_image");
        return profileImage == null ? null : profileImage.toString();
    }
}