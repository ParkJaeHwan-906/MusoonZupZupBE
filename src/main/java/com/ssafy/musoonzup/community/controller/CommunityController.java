package com.ssafy.musoonzup.community.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.musoonzup.community.dto.Community;
import com.ssafy.musoonzup.community.dto.CommunityComment;
import com.ssafy.musoonzup.community.dto.request.CommunityLike_DisLikeDto;
import com.ssafy.musoonzup.community.dto.response.CommunityCommentList;
import com.ssafy.musoonzup.community.dto.response.CommunityDetail;
import com.ssafy.musoonzup.community.dto.response.CommunityLikeDisLikeResponseDto;
import com.ssafy.musoonzup.community.dto.response.CommunityList;
import com.ssafy.musoonzup.community.service.CommunityCommentService;
import com.ssafy.musoonzup.community.service.CommunityService;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.global.security.LoginMemberDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ssafy.musoonzup.community.service.CommunityLike_DisLikeService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Community API", description = "커뮤니티 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
	// 게시글 
	private final CommunityService communityService;
	private final CommunityLike_DisLikeService communityLike_DisLikeService;
	// 댓글 
	private final CommunityCommentService commentService;
	
	/*
	 * 게시글을 작성한다.
	 * 작성에 성공하면 true, 실패하면 false 를 반환한다. 
	 */
	@Operation(
				summary = "커뮤니티 게시글 작성",
				description = "커뮤니티 게시글을 작성합니다.",
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
								), description = "게시글 제목 / 내용"
						),
				parameters = {
						@Parameter(name="Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
				},
				responses = {
						@ApiResponse(responseCode = "500", description = "게시글 작성 실패 ( 서버 오류 )"),
						@ApiResponse(responseCode = "201", description = "게시글 작성 성공")
				}
			)
	@PostMapping("/member/post")
	public ResponseEntity<Boolean> makePost(@RequestBody Community community, @AuthenticationPrincipal CustomUserDetails loginUser) {
		// 현재 로그인 상태의 유저
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		
		// 입력 정보 부족 
		if(community == null || community.getContent() == null || community.getTitle() == null ||
				community.getContent().isEmpty() || community.getTitle().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		
		// 게시글 등록 가능 
		// 부족한 정보 매핑 
		community.setMemberAccountIdx(curUser.getIdx());
		
		try {
			communityService.insert(community);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}

	@Operation(
			summary = "내가 쓴 게시글 목록 조회",
			description = "검색 조건에 따라 내가 쓴 게시글 목록을 조회한다.",
			parameters = {
//					@Parameter(name="key", description = "검색할 키워드 분류(SELECT 로 사용 [1 : 게시글 제목, 2 : 게시글 내용])", required = false),
//					@Parameter(name="value", description = "검색할 키워드 ( 검색어 )", required = false),
//					@Parameter(name="sortKey", description = "정렬 키워드 분류 ( SELECT )", required = false),
//					@Parameter(name="sortValue", description = "검색할 키워드 ( 검색어 로 사용 [1 : 작성일 순, 2 : 좋아요 순, 3 : 조회수 순, 4 : 좋아요 + 조회수 순] )", required = false),
					@Parameter(name="page", description = "조회할 페이지 번호", required = true),
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 목록 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "로그인 되지 않음"),
					@ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
			}
		)
	@GetMapping("/member/list")
	public ResponseEntity<Page<CommunityList>> myPostList(@Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		Page<CommunityList> list = null;
		try {
			list = communityService.selectAllByMemberAccountIdx(searchCondition, loginUser.getLoginMemberDto().getIdx());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	
	/*
	 * 게시글을 수정한다. 
	 */
	@Operation(
			summary = "게시글 수정 권한을 확인",
			description = "게시글을 수정할 수 있는 권한 ( 게시글 작성자인지 ) 을 확인합니다.",
			parameters = {
					@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name="idx", description = "확인할 게시글의 idx 번호 ( community 테이블의 idx )", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "게시글 작성자와 로그인 한 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "500", description = "작성자 정보는 일치하나, 게시글 조회에서 오류가 발생"),
					@ApiResponse(responseCode = "200", description = "작성자가 일치하고, 수정할 게시글 정보를 반환")
			}
		)
	// 1. 게시글 수정 권한이 있는지 확인한다. 
	@GetMapping("/member/check/{idx}")
	public ResponseEntity<CommunityDetail> checkUser(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		CommunityDetail community = null;
		try {
			community = communityService.selectByIdx(idx);
			// 작성자와 수정하려는 사용자가 일치하는지 확인 
			if(!curUser.getId().equals(community.getMemberId())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
		} catch (Exception e) {
			// 조회에서 문제가 생김 
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		
		// 일치한다면 게시글의 정보를 전달 
		return ResponseEntity.status(HttpStatus.OK).body(community);
	}
	
	// 2. 게시글을 수정한다. 
	@Operation(
			summary = "게시글 수정",
			description = "게시글을 수정한다. [이전에 권한 확인 후 반환 받은 값 (community) 에다가 title, content 내용만 변경 후 그대로 던져주세요]",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플", value = """
											{
												"idx" : "community 테이블의 idx",
												"memberAccountIdx" : "members_account 테이블의 idx",
												"title" : "제목",
												"content": "내용"
											}
											"""),
							}
							), description = "게시글 제목 / 내용"
					),
			parameters = {
				@Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER)	
			},
			responses = {
					@ApiResponse(responseCode = "400", description = "게시글 제목 혹은 내용이 빈 값으로 들어옴"),
					@ApiResponse(responseCode = "401", description = "게시글의 작성자와 현재 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "500", description = "게시글 수정 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
			}
		)
	@PutMapping("/member/edit")
	public ResponseEntity<Boolean> updatePost(@RequestBody Community community, @AuthenticationPrincipal CustomUserDetails loginUser) {
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		
		// 입력 정보 확인 
		if(community == null || community.getTitle() == null || community.getTitle().isEmpty() ||
				community.getContent() == null || community.getContent().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		
		// 유저 정보 확인 
		if(!community.getMemberId().equals(curUser.getId())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		
		// 수정 가능 
		try {
			communityService.update(community);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 게시글을 삭제한다. ( 사용자 영역 ) 
	 */
	@Operation(
			summary = "게시글 삭제",
			description = "게시글을 삭제한다.",
			parameters = {
					@Parameter(name = "idx", description = "삭제하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "게시글의 작성자와 현재 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "500", description = "게시글 삭제 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
			}
		)
	@PutMapping("/member/delete/{idx}")
	public ResponseEntity<Boolean> deletePost(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		CommunityDetail community = communityService.selectByIdx(idx);
		
		// 작성자 일치 확인 
		if(!community.getMemberId().equals(curUser.getId())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		
		// 삭제 가능
		try {
			communityService.delete(idx);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 게시글 상세 정보
	 */
	@Operation(
			summary = "게시글 상제 정보 조회",
			description = "게시글을 상세 정보를 조회한다.",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
			}
		)
	@GetMapping("/{idx}")
	public ResponseEntity<CommunityDetail> detailPost(@PathVariable Long idx) {
		CommunityDetail community = null;
		try {
			community = communityService.selectByIdx(idx);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(community);
	}
	@Operation(
			summary = "게시글 상제 정보 조회 ( 좋아요 / 싫어요 총 개수 )",
			description = "게시글을 상세 정보를 조회한다. ( 좋아요 / 싫어요 총 개수 )",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
			}
			)
	@GetMapping("/{idx}/likeDisLike")
	public ResponseEntity<Map<String, Object>> detailPostlikeDisLike(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		Map<String, Object> response = new HashMap<>();
		Integer likeOrDisLike = null;
		CommunityLikeDisLikeResponseDto communityLikeDisLikeResponseDto = null;
		try {
			/*
			 * 현재 로그인 상태의 사용자라면 
			 * 해당 게시글에 좋아요 / 싫어를 표시했는지 반환 
			 */
			if(loginUser != null) {
				likeOrDisLike = communityLike_DisLikeService.checked(loginUser.getLoginMemberDto().getIdx(), idx);
				response.put("userLikeDisLike", likeOrDisLike);
			}
			/*
			 * 현재 게시글의 좋아요, 싫어요의 총 개수를 반환
			 */
			communityLikeDisLikeResponseDto = communityLike_DisLikeService.cntLikeDisLikeByIdx(idx);
			response.put("totalLikeDisLike", communityLikeDisLikeResponseDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	/*
	 * 게시글 리스트 
	 */
	@Operation(
			summary = "게시글 목록 조회",
			description = "검색 조건에 따라 게시글 목록을 조회한다.",
			parameters = {
					@Parameter(name="key", description = "검색할 키워드 분류(SELECT 로 사용 [1 : 게시글 제목, 2 : 게시글 내용])", required = false),
					@Parameter(name="value", description = "검색할 키워드 ( 검색어 )", required = false),
					@Parameter(name="sortKey", description = "정렬 키워드 분류 ( SELECT )", required = false),
					@Parameter(name="sortValue", description = "검색할 키워드 ( 검색어 로 사용 [1 : 작성일 순, 2 : 좋아요 순, 3 : 조회수 순, 4 : 좋아요 + 조회수 순] )", required = false),
					@Parameter(name="page", description = "조회할 페이지 번호", required = true),
					@Parameter(name="Authorization", description = "로그인 상태의 토큰", required = false, in = ParameterIn.HEADER)
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 목록 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
			}
		)
	@GetMapping("/list")
	public ResponseEntity<Page<CommunityList>> postList(@Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition, @AuthenticationPrincipal CustomUserDetails loginUser) {
		Page<CommunityList> list = null;
		String role = loginUser == null ? "USER" : loginUser.getLoginMemberDto().getRole().toUpperCase();
		try {
			list = communityService.selectAll(searchCondition, role);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	/*
	 * 게시글 좋아요 조회
	 * 
	 * [반환값]
	 * 401 : 권한 없음 
	 * 500 : 서버 에러
	 * 200 : 성공
	 * 	- 0 : 싫어요
	 * 	- 1 : 좋아요
	 * 	- null : 누르지 않음 
	 */
	@Operation(
			summary = "사용자의 게시글 좋아요 / 싫어요 현황",
			description = "사용자가 현재 게시글에 좋아요 / 싫어요 를 눌렀는지, 록은 누르지 않았는지 확인한다. [0 : 싫어요, 1 : 좋아요, null : 누르지 않음]",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 조회 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
			}
		)
	@GetMapping("/member/likeDisLike/{idx}")
	public ResponseEntity<Integer> getLikeOrDisLike(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		Integer likeFlag = null;
		try {
			likeFlag = communityLike_DisLikeService.checked(loginUser.getLoginMemberDto().getIdx(), idx);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(likeFlag);
	}

	/*
	 * 좋아요 / 싫어요 등록 
	 * [Post]
	 * likeFlag 
	 * - 0 : 싫어요
	 * - 1 : 좋아요
	 */
	@Operation(
			summary = "사용자의 게시글 좋아요 / 싫어요 등록",
			description = "사용자가 현재 게시글에 좋아요 / 싫어요 를 등록한다. [true : 등록 성공, false : 등록 실패]",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER),
			},
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
							examples = {
									@ExampleObject(name="셈플1", value = """
											{
												"likeFlag" : 1
											}
											"""),
									@ExampleObject(name="셈플2", value = """
									{
										"likeFlag" : 0
									}
									"""),
							}
							), description = "[0 : 싫어요, 1 : 좋아요]"
					),
			responses = {
					@ApiResponse(responseCode = "401", description = "게시글에 좋아요 / 싫어요 권한 없음 ( 로그인 하지 않음 )"),
					@ApiResponse(responseCode = "400", description = "이미 좋아요 / 싫어요 를 눌렀음"),
					@ApiResponse(responseCode = "500", description = "게시글 좋아요 / 싫어요 등록 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 좋아요 / 싫어요 등록 성공"),
			}
		)
	@PostMapping("/member/likeDisLike/{idx}")
	public ResponseEntity<Boolean> registLikeOrDisLike(@PathVariable Long idx, @RequestBody CommunityLike_DisLikeDto req, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		try {
			communityLike_DisLikeService.registLikeOrDisLike(loginUser.getLoginMemberDto().getIdx(), idx, req.getLikeFlag());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);	
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);			
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);			
	}

	/*
	 * 좋아요 / 싫어요 취소 
	 */
	@Operation(
			summary = "사용자의 게시글 좋아요 / 싫어요 삭제",
			description = "사용자가 현재 게시글에 좋아요 / 싫어요 를 삭제한다. [true : 삭제 성공, false : 삭제 실패]",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = false, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "게시글에 좋아요 / 싫어요 취소 권한 없음 ( 로그인 하지 않음 )"),
					@ApiResponse(responseCode = "400", description = "취소할 좋아요 / 싫어요 가 없음"),
					@ApiResponse(responseCode = "500", description = "게시글 좋아요 / 싫어요 취소 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 좋아요 / 싫어요 취소 성공"),
			}
		)
	@DeleteMapping("/member/likeDisLike/{idx}")
	public ResponseEntity<Boolean> cancelLikeOrDisLike(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		if(loginUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);

		try {
			communityLike_DisLikeService.cancelLikeOrDisLike(loginUser.getLoginMemberDto().getIdx(), idx);
		} catch (IllegalAccessException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);	
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);			
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);			
	}
	//---------------- Comments ---------------------
	
	/*
	 * idx 에 해당하는 게시글의 모든 댓글을 가져온다. 
	 */
	@Operation(
			summary = "게시글의 댓글 조회",
			description = "게시글의 모든 댓글을 조회한다.",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name="page", description = "조회할 페이지 번호", required = false),
					@Parameter(name="Authorization", description = "로그인 상태의 토큰", required = false, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글의 댓글 조회 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글의 모든 댓글, 댓글 개수 조회 성공 [commentList : Page 객체, commentCnt : 댓글 개수]"),
			}
		)
	@GetMapping("/comment/{idx}")
	public ResponseEntity<Map<String, Object>> getAllComments(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser, @ModelAttribute SearchCondition_TMP searchCondition) {
		Map<String, Object> response = new HashMap<>();
		Page<CommunityCommentList> commentList = null;
		String role = loginUser == null ? "USER" : loginUser.getLoginMemberDto().getRole().toUpperCase();
		try {
			commentList = commentService.selectAllByIdx(idx, role, searchCondition);
			response.put("commentList", commentList);
			// 해당 게시글의 총 댓글 개수도 반환 
			response.put("commentCnt", commentService.selectAllByIdxCnt(idx, role));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	/*
	 * 댓글을 작성한다. 
	 * !! 프론트에서 comment 객체에 communityIdx 를 주면 @PathVariable 필요 없어요 !!
	 */
	@Operation(
			summary = "게시글에 댓글 작성",
			description = "게시글에 댓글을 작성한다. [true : 작성 성공, false : 작성 실패]",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
					requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
							content = @Content(
									examples = {
											@ExampleObject(name="셈플1", value = """
													{
														"comment" : "테스트 댓글" 
													}
													"""),
										}
									), description = "comment : 댓글 내용 작성"
							),
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글에 댓글 작성 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "400", description = "댓글 내용이 공백, 혹은 값이 없음"),
					@ApiResponse(responseCode = "201", description = "게시글에 댓글 작성 성공"),
			}
		)
	@PostMapping("/member/comment/{idx}")
	public ResponseEntity<Boolean> makeComment(@PathVariable Long idx, @RequestBody CommunityComment comment, @AuthenticationPrincipal CustomUserDetails loginUser) {
		// 현재 유저 정보
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		
		// 입력 정보 확인 
		if(comment == null || comment.getComment().isEmpty() || comment.getComment() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		
		// 부족한 정보 mapping
		comment.setMemberAccountIdx(curUser.getIdx());
		comment.setCommunityIdx(idx);
		
		try {
			commentService.insert(comment);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(true);
	}
	
	/*
	 * 댓글을 수정한다. 
	 * 
	 * 1. 작성자가 일치하는지 확인한다.
	 * 2. 댓글을 수정한다. 
	 */
	@Operation(
			summary = "게시글의 댓글 수정 권한 확인",
			description = "게시글의 댓글 작성자와 현재 사용자가 일치하는지 확인한다.",
			parameters = {
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글의 댓글 조회 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "401", description = "게시글의 댓글 작성자와 현재 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "200", description = "게시글의 댓글 작성자 검증 완료, 댓글 조회 성공"),
			}
		)
	@GetMapping("/member/check/comment/{idx}")
	public ResponseEntity<CommunityComment> checkUserComment(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		CommunityComment comment = null;
		try {
			comment = commentService.selectByIdx(idx);
			
			// 작성자 불일치
			if(!loginUser.getLoginMemberDto().getId().equals(comment.getMemberId())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		return ResponseEntity.status(HttpStatus.OK).body(comment);
	}
	@Operation(
			summary = "게시글 댓글 수정 [true : 수정 성공, false : 수정 실패]",
			description = "게시글의 댓글을 수정한다.",
			parameters = {
					@Parameter(name = "idx", description = "수정하고자하는 댓글 idx ( community_comments 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					content = @Content(
				            mediaType = "application/json",
				            examples = {
				                @ExampleObject(
				                    name = "샘플",
				                    value = """
				                        {
				                          "idx": 1,
				                          "memberAccountIdx": 1,
				                          "communityIdx": 1,
				                          "comment": "테스트 댓글"
				                        }
				                    """
				                )
				            }
				        )
			),
			responses = {
					@ApiResponse(responseCode = "401", description = "댓글 작성자와 현재 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "500", description = "게시글 수정 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
			}
		)
	@PutMapping("/member/comment/edit/{idx}")
	public ResponseEntity<Boolean> editComment(@RequestBody CommunityComment comment, 
			@AuthenticationPrincipal CustomUserDetails loginUser) {
		// 작성자 불일치
		if(!loginUser.getLoginMemberDto().getId().equals(comment.getMemberId())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		
		// 수정 가능 
		try {
			commentService.update(comment);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	/*
	 * 댓글을 삭제처리한다.
	 */
	@Operation(
			summary = "게시글 댓글 삭제 [true : 삭제 성공, false : 삭제 실패]",
			description = "게시글의 댓글을 삭제한다.",
			parameters = {
					@Parameter(name = "idx", description = "삭제하고자하는 댓글 idx ( community_comments 테이블의 idx )", required = true, in = ParameterIn.PATH),
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
			},
			responses = {
					@ApiResponse(responseCode = "401", description = "댓글 작성자와 현재 사용자가 일치하지 않음"),
					@ApiResponse(responseCode = "500", description = "게시글 삭제 중 오류가 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
			}
		)
	@PutMapping("/member/comment/delete/{idx}")
	public ResponseEntity<Boolean> deleteComment(@PathVariable Long idx, @AuthenticationPrincipal CustomUserDetails loginUser) {
		LoginMemberDto curUser = loginUser.getLoginMemberDto();
		CommunityComment comment = commentService.selectByIdx(idx);
		
		// 작성자 일치 확인 
		if(!comment.getMemberId().equals(curUser.getId())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		
		// 삭제 가능
		try {
			commentService.delete(idx);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	//----------------- ADMIN -----------------------
	
	/*
	 * 게시글 숨김 처리
	 * true : 숨김
	 * false: 공개
	 */
	@Operation(
			summary = "게시글 숨김/공개",
			description = "게시글을 숨김/공개 처리한다. [true : 숨김/공개 처리 성공, false : 숨김/공개 처리 실패]",
			parameters = {
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
			},
					requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
							content = @Content(
									examples = {
											@ExampleObject(name="샘플1", value = """
														true
													"""),
											@ExampleObject(name="샘플2", value = """
												false
											"""),
										}
									), description = "blind : [true : 숨김, false : 공개]"
							),
			responses = {
					@ApiResponse(responseCode = "500", description = "게시글 숨김 / 공개 처리 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "게시글 숨김 / 공개 처리 성공"),
			}
		)
	@PutMapping("/admin/blind/{idx}")
	public ResponseEntity<Boolean> postBlind(@PathVariable Long idx, @RequestBody Boolean blind) {
		try {
			communityService.blind(idx,blind);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	} 
	
	/*
	 * 댓글을 숨김 처리한다.
	 * true : 숨김
	 * false: 공개
	 */
	@Operation(
			summary = "게시글 댓글 숨김/공개",
			description = "게시글에 댓글을 숨김/공개 처리한다. [true : 숨김/공개 처리 성공, false : 숨김/공개 처리 실패]",
			parameters = {
					@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER),
					@Parameter(name = "idx", description = "조회하고자하는 게시글 idx ( community 테이블의 idx )", required = true, in = ParameterIn.PATH),
			},
					requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
							content = @Content(
									examples = {
											@ExampleObject(name="샘플1", value = """
														true
													"""),
											@ExampleObject(name="샘플2", value = """
												false
											"""),
										}
									), description = "blind : [true : 숨김, false : 공개]"
							),
			responses = {
					@ApiResponse(responseCode = "500", description = "댓글 숨김 / 공개 처리 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "200", description = "댓글 숨김 / 공개 처리 성공"),
			}
		)
	@PutMapping("/admin/comment/blind/{idx}")
	public ResponseEntity<Boolean> commentBlind(@PathVariable Long idx, @RequestBody Boolean blind) {
		try {
			commentService.blind(idx,blind);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
}
