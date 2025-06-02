package com.ssafy.musoonzup.member.dto;

import java.time.LocalDateTime;

import org.springframework.data.geo.Point;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegistPlaceDto {
	/*
	 * 사용자 등록 장소 DTO
	 */
	private Long idx;					// 고유 idx
	private Long memberAccountIdx;		// FK - members_account ( idx )
	private String address;				// 주소
	private String detail;				// 상세 주소
	private String alias;				// 별칭
	private Point geo;					// 주소의 좌표
	private Long delete;				// 삭제 여부 ( 0 : 삭제 X, 1 : 삭제 O ), 기본값 0
	private LocalDateTime createdAt;	// 생성일
	private LocalDateTime updatedAt;	// 갱신일 
}
