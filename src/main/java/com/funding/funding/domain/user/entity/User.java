package com.funding.funding.domain.user.entity;

import jakarta.persistence.Entity;

/**
 * [역할]
 * - 회원 정보를 저장하는 핵심 엔티티
 *
 * [포함 정보]
 * - 이메일
 * - 비밀번호
 * - 닉네임
 * - 상태 (ACTIVE, SUSPENDED, DELETED 등)
 *
 * DB 테이블과 직접 매핑된다.
 */

@Entity
public class User {

}
