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
public class CommunityCommentList {
	/*
	 * 특정 게시글의 댓글들을 반환할 DTO 
	 */
	private Long idx;					// 고유 idx ( community_comments 의 idx ) 
	private Long memberAccountIdx;		// 작성자 idx ( members_account 의 idx )
	private String memberId;			// 작성자 id  ( members_account 와 join )
	private String role;				// 작성자 권한 ( roles 의 nmae )
	private Long communityIdx;			// 게시글 idx ( community idx ) 
	private Long blind;					// 댓글 숨김 처리
	private String comment;				// 댓글
	private LocalDateTime createdAt;	// 작성일 
}
