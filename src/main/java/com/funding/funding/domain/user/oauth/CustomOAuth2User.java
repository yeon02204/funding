package com.funding.funding.domain.user.oauth;

import com.funding.funding.domain.user.dto.AuthDtos;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final AuthDtos.TokenRes tokenRes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(
            Long userId,
            AuthDtos.TokenRes tokenRes,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey
    ) {
        this.userId = userId;
        this.tokenRes = tokenRes;
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public String getName() {
        Object value = attributes.get(nameAttributeKey);
        return value == null ? String.valueOf(userId) : value.toString();
    }
}