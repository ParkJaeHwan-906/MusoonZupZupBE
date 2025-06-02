package com.ssafy.musoonzup.applyHome.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyLikeListResponseDto {
	/*
	 * 청약 공고 좋아요 리스트를 반환하기 위한 DTO
	 */
	private Long idx;					// 고유 idx
	private Long applyIdx;				// FK -> apply_home(idx)
	private String applyName;			// 공고명 -> apt Name
	private LocalDateTime createdAt;	// 생성
	private LocalDateTime updatedAt;	// 갱신일
}
