package com.ssafy.musoonzup.notice.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.musoonzup.community.dto.response.CommunityList;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.notice.dto.NoticeDto;
import com.ssafy.musoonzup.notice.dto.request.NoticeInsertRequestDto;
import com.ssafy.musoonzup.notice.dto.response.NoticeDetailResponseDto;
import com.ssafy.musoonzup.notice.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notice API", description = "공지사항 관리를 위한 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
	private final NoticeService noticeService;
	
	/*
	 * 관리자가 공지사항 작성 
	 */
	@Operation(summary = "공지사항 작성",
			description = "관리자(운영자)가 공지사항을 작성합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
											{
												"title" : "제목",
												"content": "내용"
											}
										"""),
									}
									), description = "공지사항 제목 / 내용"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰(Role : admin 이상)", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 작성 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "게시글 작성 성공")
			}
	)
	@PostMapping("/admin/post")
	public ResponseEntity<Boolean> editNotice(@RequestBody NoticeInsertRequestDto request, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null || !(loginUser.getLoginMemberDto().getRole().toUpperCase().equals("ADMIN") ||loginUser.getLoginMemberDto().getRole().toUpperCase().equals("MASTER"))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		
		try {
			noticeService.insert(request, loginUser.getLoginMemberDto().getIdx());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}
	
	/*
	 * 관리자가 공지사항 작성 
	 */
	@Operation(summary = "공지사항 수정",
			description = "관리자(운영자)가 공지사항을 수정합니다. [모든 관리자(운영자)가 수정 가능하며, 가장 마지막에 수정한 memberAccountIdx 를 업데이트합니다.]",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
											{
												"title" : "제목",
												"content": "내용"
											}
										"""),
									}
									), description = "수정할 공지사항 제목 / 내용"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰(Role : admin 이상)", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 작성 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "공지사항 작성 성공")
			}
	)
	@PutMapping("/admin/edit")
	public ResponseEntity<Boolean> postNotice(@RequestBody NoticeDto request, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null || !(loginUser.getLoginMemberDto().getRole().toUpperCase().equals("ADMIN") ||loginUser.getLoginMemberDto().getRole().toUpperCase().equals("MASTER"))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		try {
			noticeService.update(request, loginUser.getLoginMemberDto().getIdx());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}
	
	@Operation(
			summary = "공지사항 목록 조회",
			description = "검색 조건에 따라 공지사항 목록을 조회한다. [ADMIN 이상으로 로그인한 경우 blind 된 항목들도 보여진다.]",
			parameters = {
					@Parameter(name="key", description = "검색할 키워드 분류(SELECT 로 사용 [1 : 공지사항 제목, 2 : 공지사항 내용])", required = false),
					@Parameter(name="value", description = "검색할 키워드 ( 검색어 )", required = false),
					@Parameter(name="sortKey", description = "정렬 키워드 분류 ( SELECT )", required = false),
					@Parameter(name="sortValue", description = "검색할 키워드 ( 검색어 로 사용 [1 : 작성일 순, 2 : 조회수 순] )", required = false),
					@Parameter(name="page", description = "조회할 페이지 번호", required = true),
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 목록 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
			}
		)
	@GetMapping("/list")
	public ResponseEntity<Page<NoticeDetailResponseDto>> showNoticeList(@Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition, @AuthenticationPrincipal CustomUserDetails loginUser) {
		Page<NoticeDetailResponseDto> list = null;
		// 현재 로그인 한 사용자의 권한을 가져온다.
		String role = loginUser == null ? "USER" : loginUser.getLoginMemberDto().getRole();
		try {
			list = noticeService.selectAll(searchCondition, role);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@Operation(
			summary = "공지사항 상세 조회",
			description = "idx 에 해당하는 공지사항을 불러온다. [ADMIN 이상으로 로그인한 경우 blind 된 항목들도 보여진다.(프론트 처리)]",
			parameters = {
					@Parameter(name="idx", description = "조회할 notice 테이블의 idx", required = true),
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 목록 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
			}
		)
	@GetMapping("/{idx}")
	public ResponseEntity<NoticeDetailResponseDto> showNoticeDetail(@PathVariable Long idx) {
		NoticeDetailResponseDto res = null;
		// 현재 로그인 한 사용자의 권한을 가져온다.
		try {
			res = noticeService.selectByIdx(idx);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
	
	/*
	 * 관리자가 공지사항 삭제 
	 */
	@Operation(summary = "공지사항 삭제 [복구 불가]",
			description = "관리자(운영자)가 공지사항을 삭제합니다. [모든 관리자(운영자)가 삭제 가능하며, 가장 마지막에 수정한 memberAccountIdx 를 업데이트합니다.]",
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰(Role : admin 이상)", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "삭제하고자하는 notice 테이블의 idx", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 삭제 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "공지사항 작성 성공")
			}
	)
	@PutMapping("/admin/delete/{idx}")
	public ResponseEntity<Boolean> deleteNotice(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null || !(loginUser.getLoginMemberDto().getRole().toUpperCase().equals("ADMIN") ||loginUser.getLoginMemberDto().getRole().toUpperCase().equals("MASTER"))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		try {
			noticeService.delete(idx, loginUser.getLoginMemberDto());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 공지사함 숨김 / 공개 처리
	 */
	@Operation(summary = "공지사항 숨김/공개 [blind -> true : 숨김, false : 공개]",
			description = "관리자(운영자)가 공지사항을 숨김/공개 처리합니다. [모든 관리자(운영자)가 수정 가능하며, 가장 마지막에 수정한 memberAccountIdx 를 업데이트합니다.]",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="샘플", value = """
											true
										"""),
									}
									), description = "[true : 숨김, false : 공개]"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰(Role : admin 이상)", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "숨김/공개 하고자하는 notice 테이블의 idx", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 숨김/공개 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "200", description = "공지사항 숨김/공개 성공")
			}
	)
	@PutMapping("/admin/blind/{idx}")
	public ResponseEntity<Boolean> blindNotice(@PathVariable Long idx, @RequestBody Boolean blind, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null || !(loginUser.getLoginMemberDto().getRole().toUpperCase().equals("ADMIN") ||loginUser.getLoginMemberDto().getRole().toUpperCase().equals("MASTER"))) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		try {
			noticeService.blind(idx, loginUser.getLoginMemberDto(), blind);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
}

