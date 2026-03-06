package com.funding.funding.domain.project.status;

import com.funding.funding.domain.project.entity.ProjectStatus; // ✅ entity 패키지로 통일
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

// 프로젝트 상태 전이 규칙표
// - isAllowed(from, to) 만 공개 → true/false 반환
// - 예외 안 던짐, 메시지 생성 안 함 (판단만 담당)
// - final 클래스: 상속 금지 (규칙이 흔들리는 것 방지)
public final class ProjectStatusPolicy {

    // from → 가능한 to 목록
    private static final Map<ProjectStatus, Set<ProjectStatus>> ALLOWED = new EnumMap<>(ProjectStatus.class);

    static {
        ALLOWED.put(ProjectStatus.DRAFT,
                EnumSet.of(ProjectStatus.REVIEW_REQUESTED));

        ALLOWED.put(ProjectStatus.REVIEW_REQUESTED,
                EnumSet.of(ProjectStatus.APPROVED, ProjectStatus.REJECTED));

        // 반려된 프로젝트는 수정 후 재심사만 가능 (APPROVED 직행 불가)
        ALLOWED.put(ProjectStatus.REJECTED,
                EnumSet.of(ProjectStatus.REVIEW_REQUESTED));

        // 승인 이후 시작일 도달 시 FUNDING으로만 이동
        ALLOWED.put(ProjectStatus.APPROVED,
                EnumSet.of(ProjectStatus.FUNDING));

        ALLOWED.put(ProjectStatus.FUNDING,
                EnumSet.of(
                        ProjectStatus.SUCCESS,          // 마감 + 목표 달성
                        ProjectStatus.FAILED,           // 마감 + 목표 미달
                        ProjectStatus.STOPPED,          // 관리자 강제 중단
                        ProjectStatus.DELETE_REQUESTED  // 사용자 삭제 요청
                ));

        // 중단 상태 → 재개 승인 시 FUNDING으로
        ALLOWED.put(ProjectStatus.STOPPED,
                EnumSet.of(ProjectStatus.FUNDING));

        // 삭제 요청 → 환불 완료 후 DELETED로만
        ALLOWED.put(ProjectStatus.DELETE_REQUESTED,
                EnumSet.of(ProjectStatus.DELETED));

        // SUCCESS / FAILED / DELETED 는 종결 상태 → put 안 함 (기본 불허)
    }

    private ProjectStatusPolicy() {} // 인스턴스 생성 금지

    public static boolean isAllowed(ProjectStatus from, ProjectStatus to) {
        if (from == null || to == null) return false;
        return ALLOWED.getOrDefault(from, EnumSet.noneOf(ProjectStatus.class)).contains(to);
    }
}