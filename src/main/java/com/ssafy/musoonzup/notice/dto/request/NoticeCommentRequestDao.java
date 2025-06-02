package com.ssafy.musoonzup.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCommentRequestDao {
	/*
	 * 공지 사항에 댓글을 작성하기 위한 dto
	 */
	private Long memberAccountIdx;
	private String memberId;
	private Long noticeIdx;
	private String comment;
}
