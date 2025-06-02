package com.ssafy.musoonzup.applyHome.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.musoonzup.applyHome.dto.ApplyHomeComments;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeCommentsMS;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeDto;
import com.ssafy.musoonzup.applyHome.service.ApplyHomeCommentsService;
import com.ssafy.musoonzup.applyHome.service.ApplyHomeCommnetsMSService;
import com.ssafy.musoonzup.applyHome.service.ApplyHomeService;
import com.ssafy.musoonzup.applyHome.service.ApplyLikeService;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.member.dto.response.MyPageResponseDto;
import com.ssafy.musoonzup.member.service.MemberService;
import com.ssafy.musoonzup.openAi.service.GptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ApplyHome API", description = "청약 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/applyhome")
public class ApplyHomeController {
	// DB 내에 저장되어있는 endPoint 와 매핑
	private final String BASE_URL = "https://www.applyhome.co.kr/ai/aia";

	// 청약 공고
	private final ApplyHomeService applyService;
	private final ApplyLikeService applyLikeService;
	// 일반 사용자 (GUEST 포함)
	private final ApplyHomeCommentsService applyHomeCommentsService;
	// 멤버십 사용자
	private final ApplyHomeCommnetsMSService applyHomeCommnetsMSService;
	// 사용자 정보 추출
	private final MemberService mService;
	// GPT
	private final GptService gptService;

	/*
	 * 청약 공고 리스트를 불러온다.
	 * SearchCondition 을 적용하여, 필터링을 사용한다.
	 */
	@Operation(summary = "청약 공고 조회", description = "검색 조건을 반영하여 전체 청약 공고를 조회합니다.", responses = {
			@ApiResponse(responseCode = "200", description = "조회 성공"),
			@ApiResponse(responseCode = "204", description = "조회 실패, 청약 공고를 찾을 수 없음")
	}, parameters = {
			@Parameter(name = "key", description = "검색할 키워드 분류(SELECT 로 사용 [1 : 주택 관리 번호, 2 : 공고 번호, 3 : 지역, 4 : 주택명])"),
			@Parameter(name = "value", description = "검색할 키워드 ( 검색어 )"),
			@Parameter(name = "sortKey", description = "정렬 키워드 분류 ( SELECT )"),
			@Parameter(name = "sortValue", description = "검색할 키워드 ( 검색어 로 사용 [1 : 가격 순, 2 : 공고 날짜 순, 3 : 청약 시작일 순, 4 : 청약 종료일 순, 5 : 청약 당첨자 발표 순] )"),
			@Parameter(name = "page", description = "조회할 페이지 번호", required = true),
			@Parameter(name = "Authorization", description = "로그인 상태의 토큰", required = false, in = ParameterIn.HEADER)
			/*
			 * 페이지 정보는 추후 추가
			 */
	})
	@GetMapping("/list")
	private ResponseEntity<Page<ApplyHomeDto>> getList(@Parameter(hidden=true)@ModelAttribute SearchCondition_TMP searchCondition,
			@AuthenticationPrincipal CustomUserDetails loginUser) {
		String role = loginUser == null ? "USER" : loginUser.getLoginMemberDto().getRole();
		Page<ApplyHomeDto> searchList = applyService.selectAllApply(searchCondition, role);

		if (searchList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

		return ResponseEntity.status(HttpStatus.OK).body(searchList);
	}

	/*
	 * 메인 페이지에 보여줄 청약 공고 top3를 불러온다.
	 */
	@Operation(summary = "인기 청약 공고 조회", description = "조회수 / 찜 에 따른 인기 청약 공고 Top 3 를 조회합니다.")
	@GetMapping("/list/top3/{sortKey}")
	private ResponseEntity<List<ApplyHomeDto>> getTop3List(@PathVariable String sortKey) {
		List<ApplyHomeDto> searchList = applyService.selectTop3Apply(sortKey);

		if (searchList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

		return ResponseEntity.status(HttpStatus.OK).body(searchList);
	}

	/*
	 * 청약의 상세 정보를 보여준다.
	 * +
	 * 해당 청략의 조회수 또한 증가시킨다.
	 * 
	 * [25.05.15] 수정
	 * 상세 페이지 조회 시ㅣ, GPT 분석과 상세 정보를 분리해서 전달
	 */
	@Operation(summary = "청약 공고 상세 조회 ( 공고 )", description = "청약 공고의 상세 정보를 조회합니다.", responses = {
			@ApiResponse(responseCode = "404", description = "청약 정보를 찾을 수 없음"),
			@ApiResponse(responseCode = "200", description = "청약의 상세 정보 조회 성공")
	}, parameters = {
			@Parameter(name = "idx", description = "청약 공고 번호 ( apply_home 테이블의 idx )", required = true),
			@Parameter(name = "Authorization", description = "로그인한 토큰 정보 (접두사: Bearer)", required = false, in = ParameterIn.HEADER)
	})
	@GetMapping("/detail/apply")
	private ResponseEntity<Map<String, Object>> searchByIdxApply(@RequestParam Long idx,
			@AuthenticationPrincipal CustomUserDetails loginUser) {
		Map<String, Object> response = new HashMap<>();
		ApplyHomeDto applyHome = applyService.selectByIdx(idx);
		// 1. 청약의 상세 정보를 불러온다.
		if (applyHome == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		applyHome.setApplyhomeUrl(BASE_URL + applyHome.getApplyhomeUrl());

		// 1-1. 청약 정보의 좌표 정보가 없다면 조회한다.
		if (applyHome.getGeo() == null) {
			// Service 로직 작성하기 ✅
			applyHome = applyService.searchGeo(applyHome);
		}
		// 공고 정보를 저장
		response.put("pblanc", applyHome);

		/*
		 * 사용자가 로그인한 상태라면, 해당 공고를 찜 했는지 확인 후 반환한다.
		 * like 값으로 반환
		 */
		if (loginUser != null) {
			int like = applyLikeService.selectByAIdxAndmIdx(loginUser.getLoginMemberDto().getIdx(), idx);
			if (like == 1) {
				response.put("like", true);
			}
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "청약 공고 상세 조회 ( GPT )", description = "청약 공고의 상세 정보 분석 후 분석 내용을 제공합니다.", responses = {
			@ApiResponse(responseCode = "500", description = "분석 실패 ( 서버 오류 )"),
			@ApiResponse(responseCode = "200", description = "청약의 상세 정보 분석 성공")
	}, parameters = {
			@Parameter(name = "idx", description = "청약 공고 번호 ( apply_home 테이블의 idx )", required = true),
	})
	@GetMapping("/detail/gpt")
	private ResponseEntity<ApplyHomeComments> searchByIdxGpt(@RequestParam Long idx) throws InterruptedException {
		ApplyHomeDto applyHome = applyService.selectByIdx(idx);
		// 1. 청약의 상세 정보를 불러온다.
		if (applyHome == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		applyHome.setApplyhomeUrl(BASE_URL + applyHome.getApplyhomeUrl());

		// 2. 해당 청약 정보의 GPT 분석을 불러온다.
		ApplyHomeComments applyHomeComment = applyHomeCommentsService
				.selectByApplyIdx(applyHome.getIdx());

		// 2-1. 청약 정보가 없다면, GPT 를 통해 불러온 뒤, 이를 저장한다.
		if (applyHomeComment == null) {
			// GPT API 를 호출해서 해당 청약 내용의 분석을 DB (apply_home_comments에 저장한다.) ✅
			int updatedRows = applyHomeCommentsService.insert(applyHome);
			if (updatedRows == -1) { // 저장 실패
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}

			// 저장에 성공했다면, 다시 조회 후 반환
			applyHomeComment = applyHomeCommentsService
					.selectByApplyIdx(applyHome.getIdx());
		} else {	// 청약 정보가 있다면, 일관성을 위해 시간 지연 처리
			Thread.sleep(1000*3);
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(applyHomeComment);
	}

	/*
	 * 공고를 숨김처리한다.
	 */
	@Operation(summary = "공고를 숨김처리한다.", description = "(관리자 전용) 문제가 있는 공고를 숨김처리합니다.", parameters = {
			@Parameter(name = "idx", description = "공고 번호 ( apply_home 테이블의 idx )", required = true),
			@Parameter(name = "blind", description = "공고를 숨길지/공개할지 구분하는 값 [true : 숨김, false : 공개]"),
			@Parameter(name = "Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
	})
	@PutMapping("/blind")
	private ResponseEntity<Boolean> blindPblanc(@RequestParam Long idx, @RequestParam Boolean blind) {
		int updatedRows = applyService.blindPblanc(idx, blind);

		if (updatedRows == -1)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);

		return ResponseEntity.status(HttpStatus.OK).body(true);
	}

	// ----------------- MemberShip -----------------------------

	/*
	 * 현재 사용자의 현재 공고에 대한 이전에 모든 검색 결과를 가져온다.
	 * Token 에서 member_account_idx 추출하여 @RequestParam 대체 ✅
	 */
	@Operation(summary = "청약 공고 GPT 상세 분석 이력 ( Membership )", description = "GPT 상세 분석 이력을 전체 보여준다. ( Membership )", responses = {
			@ApiResponse(responseCode = "200", description = "전체 내역 조회 성공")
	}, parameters = {
			@Parameter(name = "idx", description = "청약 공고 번호 ( apply_home 테이블의 idx )", required = false),
			@Parameter(name = "Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
	})
	@GetMapping("/ms/allComments")
	private ResponseEntity<List<ApplyHomeCommentsMS>> selectAllComments(@RequestParam @Nullable Long idx,
			@AuthenticationPrincipal CustomUserDetails loginUser) {
		List<ApplyHomeCommentsMS> allComments = applyHomeCommnetsMSService
				.selectAllComments(loginUser.getLoginMemberDto().getIdx(), idx);
		return ResponseEntity.status(HttpStatus.OK)
				.body(allComments);
	}

	/*
	 * 현재 사용자가 현재 공고에 대해 검색한 내용의 결과값을 반환한다.
	 * 내부적으로는 DB 에 저장한다. -> selectAllComments() 재호출하여 리프래쉬 필요
	 * Token 에서 member_account_idx 추출하여 @RequestParam 대체 ✅
	 */
	@Operation(summary = "청약 공고 GPT 상세 분석 ( Membership )", description = "청약 공고의 상세 정보를 사용자 정의 질문을 반영하여 분석합니다.", responses = {
			@ApiResponse(responseCode = "500", description = "분석 실패 ( 서버 오류 )"),
			@ApiResponse(responseCode = "200", description = "청약의 상세 정보 분석 성공")
	}, parameters = {
			@Parameter(name = "idx", description = "청약 공고 번호 ( apply_home 테이블의 idx )", required = true),
			@Parameter(name = "request", description = "해당 청약에 대한 사용자 질문", required = true),
			@Parameter(name = "Authorization", description = "로그인 되어 있는 토큰", required = true, in = ParameterIn.HEADER)
	})
	@GetMapping("/ms/searchDetail")
	private ResponseEntity<String> seachDetail(@AuthenticationPrincipal CustomUserDetails loginUser,
			@RequestParam Long idx, @RequestParam String request) {
		ApplyHomeDto applyHome = applyService.selectByIdx(idx);
		MyPageResponseDto myInfo = mService.getMyInfo(loginUser.getLoginMemberDto());
		// 1. GPT API 를 사용하여 사용자의 추가적인 검색을 제공한다. ✅
		String gptComment = applyHomeCommnetsMSService.getGptComment(applyHome, request, myInfo.getName());
		// GPT 호출에 실패했을 때
		if (gptComment == null)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("일시적인 오류가 발생하였습니다.");

		// 2. 분석 결과를 DB(apply_home_comments_ms 에 저장한다.
		int updatedRows = applyHomeCommnetsMSService.insert(
				ApplyHomeCommentsMS.builder()
						.applyIdx(idx)
						.memberAccountIdx(loginUser.getLoginMemberDto().getIdx())
						.request(request)
						.comment(gptComment)
						.build());

		// ??? 저장 성공했는지 확인해야하나 ???
		if (updatedRows == -1)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("일시적인 오류가 발생하였습니다.");

		return ResponseEntity.status(HttpStatus.OK)
				.body(gptComment);
	}
}
