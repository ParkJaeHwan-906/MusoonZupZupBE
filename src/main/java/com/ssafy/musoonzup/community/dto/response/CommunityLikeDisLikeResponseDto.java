package com.ssafy.musoonzup.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityLikeDisLikeResponseDto {
	private Long like;		// 좋아요 개수
	private Long disLike;	// 싫어요 개수
}
