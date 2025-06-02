package com.ssafy.musoonzup.community.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityDetail {
	private Long idx;						// 고유 idx
	private Long memberAccountIdx;			// 작성자 idx ( members_account 테이블의 idx )
	private String memberId;				// 작성자 아이디 ( members_account 테이블과 join )
	private String title;					// 굴 제목
	private String content;					// 글 내용
	private Long views;						// 조회수
	private Long blind;						// 숨김처리 여부
//	private Long like;						// 좋아요 개수
//	private Long disLike;					// 싫어요 개수
//	private Long commentCnt;				// 댓글 개수
	private LocalDateTime createdAt;		// 생성일 
	private LocalDateTime updatedAt;		// 갱신일 
}
