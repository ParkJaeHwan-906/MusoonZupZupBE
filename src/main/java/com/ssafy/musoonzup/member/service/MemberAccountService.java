package com.ssafy.musoonzup.member.service;

import com.ssafy.musoonzup.global.email.service.EmailService;
import com.ssafy.musoonzup.global.redis.RedisService;
import com.ssafy.musoonzup.global.security.LoginMemberDto;
import com.ssafy.musoonzup.global.util.JwtUtil;
import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dao.RoleDao;
import com.ssafy.musoonzup.member.dto.request.ChangePasswordRequestDto;
import com.ssafy.musoonzup.member.dto.response.ChangePasswordResponseDto;
import com.ssafy.musoonzup.member.dto.request.FindPasswordRequest;
import com.ssafy.musoonzup.member.dto.response.FindPasswordResponse;
import com.ssafy.musoonzup.member.dto.response.LoginResponseDto;
import com.ssafy.musoonzup.member.dto.response.LogoutResponseDto;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberAccountService {
  private final PasswordEncoder passwordEncoder;
  private final RedisService redisService;
  private final RoleDao roleDao;
  private final MemberAccountDao memberAccountDao;
  private final EmailService mailService;

  @Transactional
  public ResponseEntity<LoginResponseDto> login(String id, String rawPw) {
    MemberAccountDto account = memberAccountDao.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("아이디나 비밀번호를 확인해주세요."));

    if (!passwordEncoder.matches(rawPw, account.getPw())) {
      throw new IllegalArgumentException("아이디나 비밀번호를 확인해주세요.");
    }
    
    if (account.getBan() == 1) {
        throw new AccessDeniedException("차단된 사용자입니다. 관리자에게 문의하세요.");
    }

    Long memberIdx = account.getMemberIdx(); // 주의: null 아닌지 확인 필요
    Long memberAccountIdx = account.getIdx();
    String accessToken = JwtUtil.createAccessToken(memberAccountIdx, memberIdx, id, account.getRole());
    String refreshToken = JwtUtil.createRefreshToken(memberAccountIdx);
    // 로그아웃할 때 삭제, 토큰 재발급을 위해 넣어둠
    redisService.setRefreshToken(memberAccountIdx, refreshToken, 7, TimeUnit.DAYS);

    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false) // 로컬 개발 중이라 false
            .path("/")
            .maxAge(Duration.ofDays(7))
            .build();

    
    
    return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new LoginResponseDto(accessToken, roleDao.findNameByIdx(account.getRole())));
  }

  public boolean isIdDuplicated(String id) {
	  if (id == null || "".equals(id)) {
		  throw new RuntimeException("입력값이 존재하지 않습니다.");
	  }
	  
    return memberAccountDao.countById(id) > 0;
  }

  @Transactional
  public LogoutResponseDto logout(Long memberAccountIdx) {
    String refreshToken = redisService.getRefreshToken(memberAccountIdx);
    if (refreshToken != null) {
      redisService.deleteRefreshToken(memberAccountIdx);
      return new LogoutResponseDto("로그아웃 되었습니다.");
    } else return new LogoutResponseDto("이미 로그아웃 되었습니다.");
  }

  public FindPasswordResponse sendTemporaryPassword(FindPasswordRequest request) {
    Optional<MemberAccountDto> optionalAccount = memberAccountDao.findByIdNameEmail(
        request.getUserId(), request.getName(), request.getEmail()
    );

    if (optionalAccount.isEmpty()) {
      return new FindPasswordResponse(false, "입력한 정보와 일치하는 회원이 존재하지 않습니다.");
    }

    String tempPassword = generateTempPassword();
    mailService.sendTempPassword(request.getEmail(), tempPassword);

    String encodedPassword = passwordEncoder.encode(tempPassword);
    memberAccountDao.updatePasswordById(request.getUserId(), encodedPassword);

    return new FindPasswordResponse(true, "임시 비밀번호가 이메일로 발송되었습니다.");
  }

  private String generateTempPassword() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  @Transactional
  public ChangePasswordResponseDto changePassword(String userId, ChangePasswordRequestDto request) {
    try {
      Optional<MemberAccountDto> optionalAccount = memberAccountDao.findById(userId);
      if (optionalAccount.isEmpty()) {
        return new ChangePasswordResponseDto(false, "해당 사용자가 존재하지 않습니다.");
      }

      MemberAccountDto account = optionalAccount.get();

      if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPw())) {
        return new ChangePasswordResponseDto(false, "현재 비밀번호가 일치하지 않습니다.");
      }

      String encodedNewPw = passwordEncoder.encode(request.getNewPassword());
      memberAccountDao.updatePasswordById(userId, encodedNewPw);

      return new ChangePasswordResponseDto(true, "비밀번호가 성공적으로 변경되었습니다.");
    } catch (Exception e) {
      return new ChangePasswordResponseDto(false, "비밀번호 변경 중 오류가 발생했습니다.");
    }
  }
  
//memberShip Upgrade
 @Transactional
 public int membershipUpgrage(LoginMemberDto loginMemberDto) throws IllegalAccessException {
	  if(loginMemberDto == null) throw new IllegalAccessException("로그인 정보가 존재하지 않습니다.");
	  
	  int updatedRows = 0;
	  try {
		  updatedRows = memberAccountDao.membershipUpgrage(loginMemberDto.getMemberIdx());
		  if(updatedRows == 0) throw new IllegalAccessException("잘못된 요청입니다.");
	  } catch(DataAccessException e) {
		  throw e;
	  }
	  return updatedRows;
 }
}