package com.ssafy.musoonzup.notice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeCommentResponseDto {
	private Long idx;
	private Long noticeIdx;
	private Long memberAccountIdx;
	private String memberId;
	private String role;
	private String comment;
	private Long blind;	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
