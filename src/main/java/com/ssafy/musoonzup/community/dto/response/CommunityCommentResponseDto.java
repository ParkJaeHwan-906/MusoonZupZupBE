package com.ssafy.musoonzup.community.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentResponseDto {
	private Long idx;
	private Long userAccountIdx;
	private String comment;
	private LocalDateTime createdAt;
}
