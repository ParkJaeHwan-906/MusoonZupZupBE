package com.ssafy.musoonzup.admin.controller;

import com.ssafy.musoonzup.admin.dto.MemberWithAccountDto;
import com.ssafy.musoonzup.admin.service.AdminService;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin API", description = "관리자 전용 사용자 관리 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService adminService;

  @Operation(
      summary = "전체 사용자 조회",
      description = "관리자 권한으로 전체 사용자 목록을 조회합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰 (ROLE_ADMIN/MASTER 필요)", required = true, in = ParameterIn.HEADER)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공"),
          @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
      }
  )
  @GetMapping("/members")
  public ResponseEntity<Page<MemberWithAccountDto>> getAllMembers(
      @ParameterObject @ModelAttribute SearchCondition_TMP condition,
      @AuthenticationPrincipal CustomUserDetails loginUser) {
    return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllMembers(loginUser, condition));
  }

  @Operation(
      summary = "사용자 차단",
      description = "관리자 권한으로 특정 사용자를 차단합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰 (ROLE_ADMIN/MASTER 필요)", required = true, in = ParameterIn.HEADER),
          @Parameter(name = "memberIdx", description = "차단할 사용자 고유 번호", required = true)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "차단 성공"),
          @ApiResponse(responseCode = "400", description = "차단 실패"),
          @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
      }
  )
  @PatchMapping("/members/{memberIdx}/ban")
  public ResponseEntity<?> banMember(@PathVariable Long memberIdx) {
    boolean result = adminService.banMember(memberIdx);
    if (!result) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("차단에 실패했습니다.");
    }
    return ResponseEntity.status(HttpStatus.OK).body("차단 되었습니다.");
  }

  @Operation(
      summary = "사용자 차단 해제",
      description = "관리자 권한으로 특정 사용자의 차단을 해제합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰 (ROLE_ADMIN/MASTER 필요)", required = true, in = ParameterIn.HEADER),
          @Parameter(name = "memberIdx", description = "해제할 사용자 고유 번호", required = true)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "차단 해제 성공"),
          @ApiResponse(responseCode = "400", description = "차단 해제 실패"),
          @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
      }
  )
  @PatchMapping("/members/{memberIdx}/unban")
  public ResponseEntity<?> unbanMember(@PathVariable Long memberIdx) {
    boolean result = adminService.unbanMember(memberIdx);
    if (!result) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("복구에 실패했습니다.");
    }
    return ResponseEntity.status(HttpStatus.OK).body("복구 되었습니다.");
  }
}
