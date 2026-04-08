package com.funding.funding.domain.project.entity;

public enum ProjectStatus {
    DRAFT,              // 작성 중
    REVIEW_REQUESTED,   // 심사 요청됨
    REJECTED,           // 심사 반려
    APPROVED,           // 심사 승인
    FUNDING,            // 후원 진행 중
    SUCCESS,            // 목표 달성 종료
    FAILED,             // 목표 미달 종료
    STOPPED,            // 관리자 강제 중단
    DELETE_REQUESTED,   // 삭제 요청됨
    DELETED             // 삭제 완료
}