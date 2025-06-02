package com.ssafy.musoonzup.community.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityComment {
	private Long idx;					// 고유 idx
	private Long memberAccountIdx;		// 작성자 idx ( members_account 의 idx )
	private String memberId;				// 작성자 Id
	private Long communityIdx;			// 게시글 idx ( community 의 idx )
	private String comment;				// 댓글
	private Long blind;					// 숨김 처리 ( 사용자 영역 )
	private Long delete;				// 삭제 처리 ( 관리자 영영 )
	private LocalDateTime createdAt;	// 생성일 
	private LocalDateTime updatedAt;	// 갱신일 
}
