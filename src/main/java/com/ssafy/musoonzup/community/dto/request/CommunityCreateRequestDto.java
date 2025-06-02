package com.ssafy.musoonzup.community.dto.request;

import lombok.Data;

@Data
public class CommunityCreateRequestDto {
	private Long userAccountIdx;
	private String content;
}
