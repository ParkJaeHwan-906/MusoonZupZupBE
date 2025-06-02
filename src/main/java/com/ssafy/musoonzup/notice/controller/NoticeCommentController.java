package com.ssafy.musoonzup.notice.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.notice.dto.request.NoticeCommentRequestDao;
import com.ssafy.musoonzup.notice.dto.response.NoticeCommentResponseDto;
import com.ssafy.musoonzup.notice.dto.response.NoticeDetailResponseDto;
import com.ssafy.musoonzup.notice.service.NoticeCommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Notice Comment API", description = "공지사항 댓글 관리를 위한 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/noticeComment")
public class NoticeCommentController {
	private final NoticeCommentService commentService;
	
	@Operation(summary = "공지사항 댓글 작성",
			description = "공지사항에 댓글을 작성합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
											{
												"content": "내용"
											}
										"""),
									}
									), description = "댓글"
					),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "댓글을 작성하려는 공지사항의 idx (notice_idx)", required = true, in=ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "댓글 작성 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "201", description = "댓글 작성 성공")
			}
	)
	@PostMapping("/member/post/{idx}")
	public ResponseEntity<Boolean> postNoticeComment(@PathVariable Long idx, @RequestBody NoticeCommentRequestDao req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		
		try {
			commentService.insert(idx, loginUser.getLoginMemberDto().getIdx(), req);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	@Operation(
			summary = "공지사항 댓글 목록 조회",
			description = "공지사항 댓글 목록을 조회한다. [ADMIN 이상으로 로그인한 경우 blind 된 항목들도 보여진다.]",
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = false, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "댓글을 조회하려는 공지사항의 idx (notice_idx)", required = true, in=ParameterIn.PATH),
					@Parameter(name="page", description = "조회할 페이지 번호", required = true),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "공지사항 목록 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
			}
		)
	@GetMapping("/{idx}")
	public ResponseEntity<Page<NoticeCommentResponseDto>> showNoticeCommentList(@PathVariable Long idx, @Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition, @AuthenticationPrincipal CustomUserDetails loginUser) {
		Page<NoticeCommentResponseDto> list = null;
		// 현재 로그인 한 사용자의 권한을 가져온다.
		String role = loginUser == null ? "USER" : loginUser.getLoginMemberDto().getRole();
		try {
			list = commentService.selectByIdx(idx, role, searchCondition);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@Operation(summary = "공지사항 댓글 삭제 [복구 불가]",
			description = "공지사항 댓글을 삭제합니다.",
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "삭제하고자하는 댓글의 idx ( notice_comments 의 idx)", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "댓글 삭제 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
			}
	)
	@PutMapping("/member/delete/{idx}")
	public ResponseEntity<Boolean> deleteNoticeComment(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		try {
			commentService.delete(idx, loginUser.getLoginMemberDto().getIdx());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	@Operation(summary = "공지사항 댓글 수정",
			description = "공지사항 댓글을 수정합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
											{
												"memberId": "test",
												"comment": "내용"
											}
									"""),
							}
			), description = "수정할 댓글"
						),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "수정하고자하는 댓글의 idx ( notice_comments 의 idx)", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "댓글 수정 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "200", description = "댓글 수정 성공")
			}
	)
	@PutMapping("/member/edit/{idx}")
	public ResponseEntity<Boolean> editNoticeComment(@PathVariable Long idx, @RequestBody NoticeCommentRequestDao req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null || !loginUser.getLoginMemberDto().getId().equals(req.getMemberId())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		try {
			req.setMemberAccountIdx(loginUser.getLoginMemberDto().getIdx());
			commentService.update(idx, req);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	@Operation(summary = "공지사항 댓글 숨김/공개 [복구 가능]",
			description = "공지사항 댓글을 숨김/공개 합니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
												true
											"""),
									}
					), description = "[true : 숨김, false : 공개]"
			),
			parameters = {
					@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "숨김/공개하고자하는 댓글의 idx ( notice_comments 의 idx)", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "댓글 숨김/공개 실패 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "권한이 없음"),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "200", description = "댓글 숨김/공개 성공")
			}
	)
	@PutMapping("/admin/blind/{idx}")
	public ResponseEntity<Boolean> blindNoticeComment(@PathVariable Long idx, @RequestBody Boolean blind, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		try {
			commentService.blind(idx, loginUser.getLoginMemberDto().getIdx(), blind);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
}
