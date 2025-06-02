package com.ssafy.musoonzup.TmapApi.dto.request;

import org.springframework.data.geo.Point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCarRouteRequestDto {
	/*
	 * Tmap API 를 호출하여, 자동차 경로를 조회합니다. 
	 * https://apis.openapi.sk.com/tmap/routes?version=1
	 * 
	 * ex)
	 * {
        "startX": 127.10323758
        "startY": 37.36520202,
        "endX": 128.87264091,
        "endY": 35.17240084,
		}
	 */
	private String startPlaceAlias;			// 출발지 명칭 / 별칭 
	private String startPlaceAddress;		// 츨발지 주소
	private Double startX;					// 출발지 좌표
	private Double startY;					// 출발지 좌표
	private String endPlaceAlias;			// 도착지 명칭 / 별칭
	private String endPlaceAddress;			// 도착지 주소
	private Double endX;					// 도착지 좌표
	private Double endY;					// 도착지 좌표
}
