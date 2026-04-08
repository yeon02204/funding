package com.funding.funding.domain.user.oauth;

import com.funding.funding.domain.user.entity.AuthProvider;

public interface OAuth2UserInfo {
    AuthProvider getProvider();
    String getProviderId();
    String getEmail();
    String getNickname();
    String getProfileImage();
}