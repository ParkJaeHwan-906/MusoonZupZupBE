package com.ssafy.musoonzup.notice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailResponseDto {
	private Long idx;
	private Long memberAccountIdx;
	private String memberId;
	private String title;
	private String content;
	private Long views;
	private Long blind;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
