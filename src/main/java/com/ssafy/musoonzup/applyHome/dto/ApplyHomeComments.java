package com.ssafy.musoonzup.applyHome.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyHomeComments {
	/*
	 * 각 공고 (apply_home) 의 분석 정보를 저장한다.
	 * 매번 같은 공고에 대해 호출할 때 GPT 를 사용하지 않고, 저장해두었다가 이를 반환한다.
	 */
	private Long idx;						// apply_home_comments 의 고유 idx
	private Long applyIdx;					// apply_home 의 idx
	private String comment;					// GPT 분석 정보
	private LocalDateTime createdAt; 		// 생성일
	private LocalDateTime updatedAt; 		// 갱신일
}
