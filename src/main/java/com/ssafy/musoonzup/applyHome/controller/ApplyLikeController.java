package com.ssafy.musoonzup.applyHome.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.musoonzup.applyHome.dto.ApplyLikeDto;
import com.ssafy.musoonzup.applyHome.dto.response.ApplyLikeListResponseDto;
import com.ssafy.musoonzup.applyHome.service.ApplyLikeService;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ApplyLike API", description = "회원별 청약 공고 게시글 찜 처리를 위한 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/applylike")
public class ApplyLikeController {
	private final ApplyLikeService applyLikeService;
	/*
	 * 찜 추가
	 */
	@Operation(
			summary = "청약 공고 찜 추가",
			description = "청약 공고를 나의 찜 항목에 추가한다.",
			parameters = {
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name = "idx", description = "추가하고자하는 공공의 idx ( apply_home 테이블의 idx )", required = true, in = ParameterIn.PATH),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "찜 처리 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "400", description = "이미 찜 한 공고 내역 ( 중복 )"),
					@ApiResponse(responseCode = "200", description = "공고 찜 성공"),
			}
		)
	@PostMapping("/{idx}")
	public ResponseEntity<Boolean> insert(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		ApplyLikeDto applyLikeDto;
		try {
			applyLikeDto = ApplyLikeDto.builder()
					.memberAccountIdx(loginUser.getLoginMemberDto().getIdx())
					.applyIdx(idx)
					.build();
			
			applyLikeService.insert(applyLikeDto);
		} catch (IllegalAccessException e) {	// 중복 데이터 존재
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} catch(Exception e) {		// 서버 오류 ( INSERT 실패 )
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 찜 삭제 
	 */
	@Operation(
			summary = "청약 공고 찜 삭제",
			description = "청약 공고를 나의 찜 항목에서 삭제한다.",
			parameters = {
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name = "idx", description = "삭제하고자하는 공공의 idx ( apply_home 테이블의 idx )", required = true, in = ParameterIn.PATH),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "삭제 처리 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "400", description = "삭제할 내역이 존재하지 않음"),
					@ApiResponse(responseCode = "200", description = "공고 삭제 성공"),
			}
		)
	@DeleteMapping("/{idx}")
	public ResponseEntity<Boolean> delete(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		
		try {
			applyLikeService.delete(loginUser.getLoginMemberDto().getIdx(), idx);
		} catch (IllegalAccessException e) {	// 삭제 데이터 존재X 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} catch(Exception e) {		// 서버 오류 ( DELETE 실패 )
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 찜 목록을 조회한다. 
	 */
	@Operation(
			summary = "청약 공고 찜 목록 조회",
			description = "나의 찜 목록(청약 공고)을 조회한다.",
			parameters = {
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name = "page", description = "조회할 페이지 번호", required = true),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "찜 목록을 불러오던 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "찜 목록 조회 성공"),
			}
		)
	@GetMapping("/list")
	public ResponseEntity<Page<ApplyLikeListResponseDto>> selectAll(@AuthenticationPrincipal CustomUserDetails loginUser, @Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		Page<ApplyLikeListResponseDto> list = null;
		try {
			list = applyLikeService.selectAll(loginUser.getLoginMemberDto().getIdx(), searchCondition);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} 
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
}
