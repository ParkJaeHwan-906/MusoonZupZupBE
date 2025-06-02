package com.ssafy.musoonzup.TmapApi.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.musoonzup.TmapApi.dto.request.FindCarRouteRequestDto;
import com.ssafy.musoonzup.TmapApi.service.TMapService;
import com.ssafy.musoonzup.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tmap API", description = "사용자의 등록 장소와, 청약 아파트 사이의 경로를 안내합니다.")
@Controller
@RequiredArgsConstructor
@RequestMapping("/route")
public class TmapApiController {
	private final TMapService mapService;
	
	@Operation(summary = "자동차 경로 안내", 
			description = "자동차 경로를 안내합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(
										    name = "샘플",
										    value = """
										        {
										            "startPlaceAlias": "출발지 명칭",
										            "startPlaceAddress": "출발지 주소",
										            "startX": 127.123,
										            "startY": 37.456,
										            "endPlaceAlias": "도착지 명칭",
										            "endPlaceAddress": "도착지 주소",
										            "endX": 127.789,
										            "endY": 37.890
										        }
										    """
										)

									}
									), description = "출발지 목적지 정보"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "경로 조회 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "경로 조회 성공")
			}
			)
	@PostMapping("/car")
	public ResponseEntity<?> findCarRoute(@RequestBody FindCarRouteRequestDto req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		Object route = null;
		try {
			route = mapService.searchNavRoute(req);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(
				Map.of(
						"startAlias", req.getStartPlaceAlias(),
						"startAddress", req.getStartPlaceAddress(),
						"endAlias", req.getEndPlaceAlias(),
						"endAddress", req.getEndPlaceAddress(),
						"route", route
						)
				);
	}
	
	@Operation(summary = "보행자 경로 안내", 
			description = "보행자 경로를 안내합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(
										    name = "샘플",
										    value = """
										        {
										            "startPlaceAlias": "출발지 명칭",
										            "startPlaceAddress": "출발지 주소",
										            "startX": 127.123,
										            "startY": 37.456,
										            "endPlaceAlias": "도착지 명칭",
										            "endPlaceAddress": "도착지 주소",
										            "endX": 127.789,
										            "endY": 37.890
										        }
										    """
										)

									}
									), description = "출발지 목적지 정보"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "경로 조회 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "경로 조회 성공")
			}
			)
	@PostMapping("/pedestrian")
	public ResponseEntity<?> findPedestrianRoute(@RequestBody FindCarRouteRequestDto req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		Object route = null;
		try {
			route = mapService.searchPedestrianNavRoute(req);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(
				Map.of(
						"startAlias", req.getStartPlaceAlias(),
						"startAddress", req.getStartPlaceAddress(),
						"endAlias", req.getEndPlaceAlias(),
						"endAddress", req.getEndPlaceAddress(),
						"route", route
						)
				);
	}
	
	@Operation(summary = "대중교통 경로 안내", 
			description = "대중교통 경로를 안내합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(
										    name = "샘플",
										    value = """
										        {
										            "startPlaceAlias": "출발지 명칭",
										            "startPlaceAddress": "출발지 주소",
										            "startX": 127.123,
										            "startY": 37.456,
										            "endPlaceAlias": "도착지 명칭",
										            "endPlaceAddress": "도착지 주소",
										            "endX": 127.789,
										            "endY": 37.890
										        }
										    """
										)

									}
									), description = "출발지 목적지 정보"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "경로 조회 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "경로 조회 성공")
			}
			)
	@PostMapping("/transit")
	public ResponseEntity<?> findTransitRoute(@RequestBody FindCarRouteRequestDto req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		Object route = null;
		try {
			route = mapService.searchTransitRoute(req);
		} catch(Exception e) {
//			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(
				Map.of(
						"startAlias", req.getStartPlaceAlias(),
						"startAddress", req.getStartPlaceAddress(),
						"endAlias", req.getEndPlaceAlias(),
						"endAddress", req.getEndPlaceAddress(),
						"route", route
						)
				);
	}
}
