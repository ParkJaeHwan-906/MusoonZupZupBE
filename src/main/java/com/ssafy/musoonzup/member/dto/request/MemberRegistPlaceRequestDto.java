package com.ssafy.musoonzup.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegistPlaceRequestDto {
	/*
	 * member_regist_place 저장을 위한 요청 DTO
	 * 별칭
	 * 주소 
	 * 정보만 받아온다. 
	 */
	private String alias;
	private String address;
	private String detail;
}
