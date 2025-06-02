
package com.ssafy.musoonzup.auth.controller;

import com.ssafy.musoonzup.global.redis.RedisService;
import com.ssafy.musoonzup.global.util.JwtUtil;
import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth API", description = "인증 관련 API (토큰 갱신 등)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final RedisService redisService;
  private final MemberAccountDao memberAccountDao;

  @Operation(
      summary = "Access Token 재발급",
      description = """
          브라우저 쿠키에 포함된 유효한 Refresh Token을 기반으로 새로운 Access Token을 발급합니다.

          - JS에서 직접 Authorization 헤더를 보낼 필요 없이, 쿠키에 있는 refreshToken이 자동 전송됩니다.
          - 유효하지 않거나 만료된 토큰일 경우 401 응답을 반환합니다.
          """,
      parameters = {
          @Parameter(name = "refreshToken", description = "쿠키에서 자동 전송되는 Refresh Token", required = true, in = ParameterIn.COOKIE)
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공"),
          @ApiResponse(responseCode = "400", description = "refreshToken 쿠키 없음"),
          @ApiResponse(responseCode = "401", description = "토큰 만료 또는 유효하지 않음"),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류")
      }
  )
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshAccessToken(
      @Parameter(hidden = true) HttpServletRequest request
  ) {
    String refreshToken = extractRefreshTokenFromCookie(request);
    if (refreshToken == null) {
      return ResponseEntity.badRequest().body("refreshToken 쿠키가 존재하지 않습니다.");
    }

    try {
      Map<String, Object> payload = JwtUtil.parseEncryptedPayload(refreshToken);
      Long memberAccountIdx = Long.parseLong(payload.get("memberAccountIdx").toString());

      String savedRefreshToken = redisService.getRefreshToken(memberAccountIdx);

      if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 만료되었거나 유효하지 않습니다.");
      }

      MemberAccountDto account = memberAccountDao.findByIdx(memberAccountIdx)
          .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

      String newAccessToken = JwtUtil.createAccessToken(
          account.getIdx(), account.getMemberIdx(), account.getId(), account.getRole()
      );

      return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", newAccessToken));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 재발급에 실패했습니다.");
    }
  }

  /*
   * Swagger 문서를 위해 @CookieValue 어노테이션 대신 servelt request로 refreshtoken 추출 선택
   * */
  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}