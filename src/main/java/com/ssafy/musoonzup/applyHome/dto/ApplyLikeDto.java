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
public class ApplyLikeDto {
	/*
	 * 청약 공고 좋아요 처리를 위한 DTO
	 * apply_like 테이블 DTO
	 */
	private Long idx;					// 고유 idx
	private Long memberAccountIdx;		// FK -> members_account(idx)
	private Long applyIdx;				// FK -> apply_home(idx)
	private LocalDateTime createdAt;	// 생성
	private LocalDateTime updatedAt;	// 갱신일 
}
