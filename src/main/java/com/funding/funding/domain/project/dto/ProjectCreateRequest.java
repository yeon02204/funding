package com.funding.funding.domain.project.dto;

import java.time.LocalDateTime;

// 역할 : POST / Projects 할 때 JSON으로 들어오는 값을 스프링이 이 객체에 담는다
// 컨트롤러가 요청 값을 받기 위한 DTO

public class ProjectCreateRequest { // 사용자가 보낸 JSON 데이터를 스프링이 객체로 변환해서 넣어줌
    private Long goalAmount; 
    private LocalDateTime startAt;
    private LocalDateTime deadline; // 새로 추가
    
    public Long getGoalAmount() { // 목표금액
    	return goalAmount; 
    }
    
    public LocalDateTime getStartAt() { // 펀딩 시작 시간
        return startAt;
    }

    public LocalDateTime getDeadline() { // 펀딩 종료 시간
        return deadline;
    }
}