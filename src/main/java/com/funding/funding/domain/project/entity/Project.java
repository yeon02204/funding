package com.funding.funding.domain.project.entity;

import jakarta.persistence.Entity;

/**
 * [역할]
 * - 프로젝트 정보를 저장하는 엔티티
 *
 * [포함 정보]
 * - 제목
 * - 목표 금액
 * - 현재 금액
 * - 마감일
 * - 상태 (DRAFT, FUNDING, SUCCESS, FAILED 등)
 */
@Entity
public class Project {
}