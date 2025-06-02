package com.ssafy.musoonzup.community.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityList {
	/*
	 * community 테이블의 게시글 리스트를 반환하는 DTO
	 */
	private Long idx;					// community idx 
	private String title;				// 제목
	private String memberId;			// 작성자 ID 
	private Long views;					// 조회수
	private Long like;					// 좋아요 개수
	private Long disLike;				// 싫어요 개수
	private Long blind;					// 숨김처리 여부
	private Long commentCnt;			// 댓글수
	private LocalDateTime createdAt;	// 작성일 
}
