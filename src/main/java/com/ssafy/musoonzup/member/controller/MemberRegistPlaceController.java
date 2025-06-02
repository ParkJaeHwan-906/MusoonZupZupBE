package com.ssafy.musoonzup.member.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.musoonzup.community.service.CommunityCommentService;
import com.ssafy.musoonzup.global.interceptor.TimestampInterceptor;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.global.security.LoginMemberDto;
import com.ssafy.musoonzup.member.dto.MemberRegistPlaceDto;
import com.ssafy.musoonzup.member.dto.request.MemberRegistPlaceRequestDto;
import com.ssafy.musoonzup.member.dto.response.MemberRegistPlaceListResponseDto;
import com.ssafy.musoonzup.member.service.MemberRegistPlaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@Tag(name="Member Regist Place API", description = "사용자의 관심 장소를 등록하고 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/registPlace")		// MembersController 의 RequestMapping 하고 동일 
public class MemberRegistPlaceController {
	private final MemberRegistPlaceService memberRegistPlaceService;

	/*
	 * 사용자의 등록 장소 저장 
	 */
	@Operation(
			summary = "사용자 지정 장소 등록",
			description = "사용자가 관심 장소 / 자주가는 장소 를 등록한다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="샘플", value = """
											{
												"alias" : "별칭",
												"address": "주소 [도로명 / 지번]",
												"detail" : "상세주소 ex) 1층 바나프레소"
											}
											"""),
							}
							), description = "[alias : 등록할 장소 별명, address : 주소 [도로명 / 지번]]"
					),
			parameters = {
				@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER)	
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "장소 등록 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "장소 등록 성공"),
			}
		)
	@PostMapping("/regist")
	public ResponseEntity<Boolean> registPersonalPlace(@RequestBody MemberRegistPlaceRequestDto registReq,
			@AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		System.out.println(registReq);
		// 쳔재 로그인 한 사용자 
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		try {
			// DTO 세팅 
			MemberRegistPlaceDto memberRegistPlaceDto = MemberRegistPlaceDto.builder()
					.memberAccountIdx(curUser.getIdx())
					.alias(registReq.getAlias())
					.address(registReq.getAddress())
					.detail(registReq.getDetail())
					.build();
			
			memberRegistPlaceService.insert(memberRegistPlaceDto);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 현 사용자가 등록한 장소의 개수를 반환 
	 * 한 명의 사용자가 3개를 초과하는 장소를 등록할 수 없다. 
	 */
	@Operation(
			summary = "등록 장소 개수 조회",
			description = "사용자가 등록한 장소의 개수를 반환합니다. 한 명의 사용자는 3개를 초과하는 장소를 등록할 수 없습니다.",
			parameters = {
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "로그인 되어있지 않음."),
					@ApiResponse(responseCode = "500", description = "사용자의 등록 장수 개수 반환 중 오류 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "사용자가 등록한 장소의 개수를 반환한다.")
			}
		)
	@GetMapping("/cnt")
	public ResponseEntity<Integer> getRegistCnt(@AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		Integer registCnt = null;
		try {
			registCnt = memberRegistPlaceService.getRegistCnt(curUser.getIdx());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(registCnt);
	}
	@Operation(
			summary = "등록 장소 삭제",
			description = "등록한 장소를 삭제합니다.",
			parameters = {
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "확인할 게시글의 idx 번호 ( community 테이블의 idx )", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "로그인 되어있지 않음."),
					@ApiResponse(responseCode = "500", description = "등록 장소 삭제 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "장소 삭제 성공")
			}
		)
	@PutMapping("/delete/{idx}")
	public ResponseEntity<Boolean> delete(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		
		try {
			memberRegistPlaceService.deletePlace(curUser.getIdx(), idx);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	@Operation(
			summary = "등록 장소 목록 조회",
			description = "사용자가 등록한 장소의 목록을 반환합니다.",
			parameters = {
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "로그인 되어있지 않음."),
					@ApiResponse(responseCode = "500", description = "사용자의 등록 장수 목록 반환 중 오류 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "사용자가 등록한 장소의 목록 조회 완료.")
			}
		)
	@GetMapping("")
	public ResponseEntity<List<MemberRegistPlaceListResponseDto>> getRegistPlace(@AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		List<MemberRegistPlaceListResponseDto> registPlace = null;
		try {
			registPlace = memberRegistPlaceService.selectByMemeberAccountIdx(curUser.getIdx());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(registPlace);
	}
	
}
