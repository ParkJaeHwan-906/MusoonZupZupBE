package com.ssafy.musoonzup.applyHome.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.geo.Point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyHomeDto {
	private Long idx;							// 고유 idx
	private Long houseManageNo;					// 주택 관리 번호
	private Long pblancNo;						// 공고 번호
	private Long views;							// 조회수
	private Long blind;							// 숨김 여부 ( 0 : 공개, 1 : 숨김 )
	private String houseName;					// 주택 명
	private Long houseCode;						// 주택 코드 ( 4 : 무순위, 6 : 불법행위 재공급 )
	private Long zipCode;						// 우편 번호
	private Long sidoCode;						// 지역 코드
	private String houseAddress;				// 주택 주소
	private Point geo;							// 좌표 정보 
	private Long suplyCount;					// 공급 대수
	private Long suplyPrice;					// 공급 가격
	private LocalDate pblancDate;				// 공고 날짜
	private LocalDate applyStartDate;			// 청약 시작일
	private LocalDate applyEndDate;				// 청약 종료일
	private LocalDate applyAnnounceDate;		// 청약 발표일
	private String businessEntity;				// 사업 주체
	private String businessTel;					// 문의처
	private String applyhomeUrl;				// 청약 홈 url
	private Long likeCnt;						// 찜 개수
	// global
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
