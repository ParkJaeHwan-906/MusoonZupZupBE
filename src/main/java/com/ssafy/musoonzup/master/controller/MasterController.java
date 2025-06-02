package com.ssafy.musoonzup.master.controller;

import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.master.dto.RoleChangeRequestDto;
import com.ssafy.musoonzup.master.dto.RoleChangeResponseDto;
import com.ssafy.musoonzup.master.service.MasterService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master API", description = "마스터 권한 전용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master")
public class MasterController {
  private final MasterService masterService;

  @Operation(
      summary = "사용자 권한 변경",
      description = """
          마스터 권한으로 특정 사용자의 역할(Role)을 변경합니다.

          - MASTER 계정만 호출할 수 있습니다.
          - 요청 바디에는 사용자 식별자 및 변경할 권한 정보가 포함되어야 합니다.
          """,
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰 (MASTER 권한 필요)", required = true, in = ParameterIn.HEADER)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
          @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 변경 실패"),
          @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
      }
  )
  @PatchMapping("/role")
  public ResponseEntity<RoleChangeResponseDto> changeRole(
      @AuthenticationPrincipal CustomUserDetails loginUser,
      @RequestBody RoleChangeRequestDto roleChangeRequestDto) {

    RoleChangeResponseDto responseDto = masterService.changeRole(loginUser, roleChangeRequestDto);

    if (!responseDto.isChanged()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }
}

