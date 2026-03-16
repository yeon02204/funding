package com.funding.funding.domain.project.dto;

import java.time.LocalDateTime;

/*
 * 프로젝트 수정할 때 클라이언트가 보내는 데이터를 담기 위한 클래스
 * 
 * */

public record ProjectUpdateRequest(
        String title,
        String content,
        Long goalAmount,
        LocalDateTime startAt,
        LocalDateTime deadline,
        Long categoryId
) {}