package com.ssafy.musoonzup.community.dto.request;

import lombok.Data;

@Data
public class CommentCreateRequestDto {
	private Long userAccountIdx;
	private Long communityIdx;
	private String comment;
}
