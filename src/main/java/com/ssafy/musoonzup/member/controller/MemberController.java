package com.ssafy.musoonzup.member.controller;

import com.ssafy.musoonzup.global.email.dto.EmailDto;
import com.ssafy.musoonzup.global.email.dto.VerifyRequestDto;
import com.ssafy.musoonzup.global.email.service.EmailService;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.global.security.LoginMemberDto;
import com.ssafy.musoonzup.member.dto.request.ChangePasswordRequestDto;
import com.ssafy.musoonzup.member.dto.response.ChangePasswordResponseDto;
import com.ssafy.musoonzup.member.dto.request.FindPasswordRequest;
import com.ssafy.musoonzup.member.dto.response.FindPasswordResponse;
import com.ssafy.musoonzup.member.dto.request.LoginRequestDto;
import com.ssafy.musoonzup.member.dto.response.LogoutResponseDto;
import com.ssafy.musoonzup.member.dto.request.RegisterRequestDto;
import com.ssafy.musoonzup.member.dto.response.MyPageResponseDto;
import com.ssafy.musoonzup.member.service.MemberAccountService;
import com.ssafy.musoonzup.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member API", description = "Member 관리를 위한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
  private final MemberAccountService memberAccountService;
  private final MemberService memberService;
  private final EmailService emailService;

  @Operation(
      summary = "회원가입",
      description = "신규 회원을 등록합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "회원가입 성공"),
          @ApiResponse(responseCode = "400", description = "회원가입 실패")
      }
  )
  @PostMapping("/register")
  public ResponseEntity<String> registerMember(@RequestBody RegisterRequestDto dto) {
    try {
      memberService.registerMember(dto);
      return ResponseEntity.status(HttpStatus.OK).body("회원가입 완료");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 처리 중 오류가 발생했습니다.");
    }
  }

  @Operation(
      summary = "이메일 인증코드 발송",
      description = "사용자의 이메일로 인증코드를 발송합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "인증코드 발송 성공"),
          @ApiResponse(responseCode = "400", description = "발송 실패 또는 요청 과다")
      }
  )
  @PostMapping("/send-verification")
  public ResponseEntity<String> sendVerification(@RequestBody EmailDto dto) {
    try {
      emailService.sendVerificationCode(dto.getEmail());
      return ResponseEntity.status(HttpStatus.OK).body("인증코드 발송 완료");
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 요청을 너무 자주 보냈습니다. 잠시 후 다시 시도해주세요.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드 발송 중 오류가 발생했습니다.");
    }
  }

  @Operation(
      summary = "이메일 인증코드 검증",
      description = "사용자가 입력한 인증코드가 유효한지 확인합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "인증코드 유효"),
          @ApiResponse(responseCode = "400", description = "인증코드 오류")
      }
  )
  @PostMapping("/verify-code")
  public ResponseEntity<Boolean> verifyCode(@RequestBody VerifyRequestDto dto) {
    try {
      boolean isValid = emailService.verifyCode(dto.getEmail(), dto.getCode());
      return ResponseEntity.status(HttpStatus.OK).body(isValid);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }
  }

  @Operation(
      summary = "아이디 중복 확인",
      description = "입력한 아이디의 중복 여부를 확인합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "사용 가능한 아이디"),
          @ApiResponse(responseCode = "409", description = "중복된 아이디"),
          @ApiResponse(responseCode = "400", description = "요청 오류")
      }
  )
  @GetMapping("/check-id")
  public ResponseEntity<?> checkIdDuplicate(@RequestParam String id) {
    try {
      boolean isDuplicated = memberAccountService.isIdDuplicated(id);
      if (isDuplicated) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
      } else {
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디입니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디 중복 체크 중 오류가 발생했습니다.");
    }
  }

  @Operation(
      summary = "로그인",
      description = "아이디와 비밀번호로 로그인합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "로그인 성공"),
          @ApiResponse(responseCode = "400", description = "로그인 실패"),
          @ApiResponse(responseCode = "403", description = "접근 권한 없음")
      }
  )
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
    try {
      return memberAccountService.login(dto.getId(), dto.getPw());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그인 처리 중 오류가 발생했습니다.");
    }
  }

  @Operation(
      summary = "로그아웃",
      description = "현재 로그인된 사용자를 로그아웃 처리합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰", required = true, in = ParameterIn.HEADER)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
          @ApiResponse(responseCode = "400", description = "로그아웃 실패")
      }
  )
  @PostMapping("/logout")
  public ResponseEntity<LogoutResponseDto> logout(@AuthenticationPrincipal CustomUserDetails loginUser) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(
          memberAccountService.logout(loginUser.getLoginMemberDto().getIdx()));
    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LogoutResponseDto(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LogoutResponseDto("로그아웃 처리 중 오류가 발생했습니다."));
    }
  }

  @Operation(
      summary = "비밀번호 찾기",
      description = "가입된 이메일로 임시 비밀번호를 발급합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "임시 비밀번호 발송 성공"),
          @ApiResponse(responseCode = "400", description = "처리 실패")
      }
  )
  @PostMapping("/find-password")
  public ResponseEntity<FindPasswordResponse> findPassword(@RequestBody FindPasswordRequest request) {
    try {
      FindPasswordResponse response = memberAccountService.sendTemporaryPassword(request);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  @Operation(
      summary = "비밀번호 변경",
      description = "현재 로그인한 사용자가 비밀번호를 변경합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰", required = true, in = ParameterIn.HEADER)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
          @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패")
      }
  )
  @PatchMapping("/change-password")
  public ResponseEntity<ChangePasswordResponseDto> changePassword(
      @AuthenticationPrincipal CustomUserDetails loginUser,
      @RequestBody ChangePasswordRequestDto request) {
    try {
      String userId = loginUser.getLoginMemberDto().getId();
      ChangePasswordResponseDto response = memberAccountService.changePassword(userId, request);

      if (response.isSuccess()) {
        return ResponseEntity.status(HttpStatus.OK).body(response);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ChangePasswordResponseDto(false, "패스워드 변경 처리 중 오류가 발생했습니다."));
    }
  }

  @Operation(
      summary = "마이페이지 조회",
      description = "현재 로그인한 사용자의 정보를 반환합니다.",
      parameters = {
          @Parameter(name = "Authorization", description = "Bearer 토큰", required = true, in = ParameterIn.HEADER)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공"),
          @ApiResponse(responseCode = "400", description = "조회 실패")
      }
  )
  @GetMapping("/mypage")
  public ResponseEntity<MyPageResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails loginUser) {
    try {
      MyPageResponseDto dto = memberService.getMyInfo(loginUser.getLoginMemberDto());
      return ResponseEntity.status(HttpStatus.OK).body(dto);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }
  
  @Operation(summary = "멤버십 업데이트", description = "일반 USER 를 MEMBERSHIP 으로 업그레이드 합니다.",
		  parameters = {
				  @Parameter(name="Authorization", description = "로그인한 토큰 정보 (점두사: Bearer)", required = true, in = ParameterIn.HEADER)
		  },
		  responses = {
					@ApiResponse(responseCode = "500", description = "멤버십 전환 중 오류 발생 ( 서버 오류 )"),
					@ApiResponse(responseCode = "400", description = "잘못된 요청"),
					@ApiResponse(responseCode = "200", description = "멤버십 전환 성공"),
			}
		  )
  @PatchMapping("/upgrade")
  public ResponseEntity<Boolean> membershipUpgrage(@AuthenticationPrincipal CustomUserDetails loginUser) {
	  if(loginUser == null || !loginUser.getLoginMemberDto().getRole().equals("USER")) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
	  }
	  
	  try {
		  memberAccountService.membershipUpgrage(loginUser.getLoginMemberDto());
	  } catch(DataAccessException e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
	  } catch(IllegalAccessException e) {
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
	  }
	  
	  return ResponseEntity.status(HttpStatus.OK).body(true);
  }
}
