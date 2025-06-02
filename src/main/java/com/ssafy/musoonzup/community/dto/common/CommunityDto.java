package com.ssafy.musoonzup.community.dto.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDto {
	private Long idk;
	private Long userAccountIdx;
	private String content;
	private Integer blind;
	private Integer delete;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
