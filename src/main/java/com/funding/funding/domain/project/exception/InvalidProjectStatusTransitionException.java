package com.funding.funding.domain.project.exception;

import com.funding.funding.domain.project.entity.ProjectStatus; // ✅ entity 패키지로 통일
import lombok.Getter;

// 허용되지 않은 상태 전이 시 던지는 커스텀 예외
// - from: 현재 상태
// - to: 이동하려 했던 상태
// 로그, 디버깅, 에러 응답에서 어떤 전이가 실패했는지 추적 가능
@Getter
public class InvalidProjectStatusTransitionException extends RuntimeException {

    private final ProjectStatus from;
    private final ProjectStatus to;

    public InvalidProjectStatusTransitionException(ProjectStatus from, ProjectStatus to) {
        super("허용되지 않은 상태 전이입니다. [" + from + " → " + to + "]");
        this.from = from;
        this.to = to;
    }
}



// ===========================
// 성혁님 공부용 주석 및 설명 주석 살리기
// ===========================
/*
// 예외 클래스 선언
@Getter
public class InvalidProjectStatusTransitionException extends RuntimeException {
    // 외부(예 : 예외 핸들러, 로깅 코드)에서 from 값을 읽을 수 있게 하는 getter
    // 저장해둔 from 상태 반환
    // 상태 전이 불가 상황을 표현하는 커스텀 런타임 예외
    private final ProjectStatus from; // 어디서(From)
    // 외부에서 to 값을 읽을 수 있게 하는 getter
    // 저장해둔 to 상태 반환
    private final ProjectStatus to;   // 어디로(to) 가려다 실패했는지 기록하기 위한 필드
    								  // 로그, 디버깅, 에러 응답 만들 때 유용
    								  
    public InvalidProjectStatusTransitionException(ProjectStatus from, ProjectStatus to) {  // 생성자, 이 예외를 던질 때 from/to를 반듴시 넘기게 강제하는 구조
    																					    // 그래서 예외가 항상 무슨 전이였는지 정보를 갖게 됨	
    	super("허용되지 않은 상태 전이입니다. [" + from + " → " + to + "]");                       // RuntimeException의 생성자 호출 // 예외가 터지면 로그에 이 메시지가 찍혀서 어떤 전이가 문제였는지 확인
        this.from = from;  															        // 생성자로 받은 from 값을 필드에 저장, this.from은 클래스 필드, 오른쪽 from은 생성자 파라미터
        this.to = to;     																	// 생성자로 받은 to 값을 필드에 저장, this.to는 클래스 필드, 오른쪽 to는 생성자 파라미터
    }

}
 */