package com.ssafy.musoonzup.notice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDto {
	private Long idx;
	private Long memberAccountIdx;
	private String title;
	private String content;
	private Long views;
	private Long blind;
	private Long delete;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
