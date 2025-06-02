package com.ssafy.musoonzup.member.service;

import com.ssafy.musoonzup.global.email.service.EmailService;
import com.ssafy.musoonzup.global.security.LoginMemberDto;
import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dao.MemberDao;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import com.ssafy.musoonzup.member.dto.MemberDto;
import com.ssafy.musoonzup.member.dto.request.RegisterRequestDto;
import com.ssafy.musoonzup.member.dto.response.MyPageResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final EmailService emailService;
  private final MemberDao memberDao;
  private final PasswordEncoder passwordEncoder;
  private final MemberAccountDao memberAccountDao;

  @Transactional
  public void registerMember(RegisterRequestDto dto) {

    if (!emailService.isEmailVerified(dto.getEmail())) {
      throw new IllegalStateException("이메일 인증이 필요합니다.");
    }

    //api 검증 이후에 service에서도 한번 더 검증 후에 회원가입 진행
    if (memberAccountDao.countById(dto.getId()) > 0) {
      throw new IllegalStateException("이미 사용 중인 아이디입니다.");
    }

    MemberDto member = MemberDto.builder()
        .name(dto.getName())
        .email(dto.getEmail())
        .phone(dto.getPhone())
        .gender(dto.getGender())
        .birthDate(dto.getBirthDate())
        .build();
    memberDao.insert(member); // member.idx 생성됨

    MemberAccountDto account = MemberAccountDto.builder()
        .memberIdx(member.getIdx())
        .id(dto.getId())
        .pw(passwordEncoder.encode(dto.getPw()))
        .role(1L) // 기본 user 권한
        .build();
    memberAccountDao.insert(account);

    // 남아있을 이메일 인증 번호라던지 인증 여부(true/false) 삭제
    emailService.clearVerificationData(dto.getEmail());
  }
  
  @Transactional(readOnly = true)
  public MyPageResponseDto getMyInfo(LoginMemberDto loginMemberDto) {
	Long memberIdx = loginMemberDto.getMemberIdx();
    MemberDto member = memberDao.findByIdx(memberIdx)
        .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

    String gender = (member.getGender() == 0) ? "남성" : "여성";

    return MyPageResponseDto.builder()
        .name(member.getName())
        .email(member.getEmail())
        .id(memberAccountDao.findIdByMemberIdx(memberIdx))
        .gender(gender)
        .role(loginMemberDto.getRole())
        .build();
  }
}
