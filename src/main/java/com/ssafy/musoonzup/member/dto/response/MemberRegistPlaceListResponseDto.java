package com.ssafy.musoonzup.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegistPlaceListResponseDto {
	private Long idx;				// 고유 idx
	private Long memberAccountIdx;	// FK -> members_account(idx) 
	private String memberId;		// members_account(id)
	private String alias;			// 장소 별칭
	private String address;			// 주소
	private Double x;				// x 좌표
	private Double y;				// y 좌표
}
