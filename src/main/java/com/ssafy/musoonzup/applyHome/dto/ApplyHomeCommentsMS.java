package com.ssafy.musoonzup.applyHome.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyHomeCommentsMS {
	private Long idx;
	private Long applyIdx;
	private String houseName;
	private Long memberAccountIdx;
	private String request;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
