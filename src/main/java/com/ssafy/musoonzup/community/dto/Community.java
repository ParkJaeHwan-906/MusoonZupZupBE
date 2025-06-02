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
public class Community {
	/*
	 *	community 테이블 
	 */
	private Long idx;						// 고유 idx
	private Long memberAccountIdx;			// 작성자 idx ( members_account 테이블의 idx )
	private String memberId;				// 작성자 아이디 ( members_account 테이블과 join )
	private String title;					// 굴 제목
	private String content;					// 글 내용
	private Long views;						// 조회수
	private Long blind;						// 숨김 처리 여부 ( 관리자 영역 )
	private Long delete;					// 삭제 처리 여부 ( 사용자 영역 ) 
	private LocalDateTime createdAt;		// 생성일 
	private LocalDateTime updatedAt;		// 갱신일 
}
