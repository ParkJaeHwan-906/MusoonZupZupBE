package com.ssafy.musoonzup.notice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCommentDto {
	private Long idx;
	private Long memberAccountIdx;
	private Long noticeIdx;
	private String comment;
	private Long blind;
	private Long delete;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
