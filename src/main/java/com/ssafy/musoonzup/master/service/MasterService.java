package com.ssafy.musoonzup.master.service;

import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.master.dto.RoleChangeRequestDto;
import com.ssafy.musoonzup.master.dto.RoleChangeResponseDto;
import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dao.MemberDao;
import com.ssafy.musoonzup.member.dao.RoleDao;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import com.ssafy.musoonzup.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MasterService {
  private final RoleDao roleDao;
  private final MemberDao memberDao;
  private final MemberAccountDao memberAccountDao;

  @Transactional
  public RoleChangeResponseDto changeRole(CustomUserDetails loginUser, RoleChangeRequestDto roleChangeRequestDto) {
    if (!loginUser.getLoginMemberDto().getRole().equals("MASTER"))
      throw new RuntimeException("바꿀 권한이 없는 사용자입니다.");

    MemberDto memberDto = memberDao.findByIdx(roleChangeRequestDto.getMemberIdx())
                  .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

    MemberAccountDto memberAccountDto = memberAccountDao.findByMemberIdx(roleChangeRequestDto.getMemberIdx())
        .orElseThrow(() -> new IllegalArgumentException("계정 정보가 존재하지 않습니다."));


    // TODO: role 자체를 Integer로 둘지
    Long newRoleIdx = roleDao.findIdxByName(roleChangeRequestDto.getToRole().toUpperCase())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 role입니다."));

    // 이미 같은 권한이면 false 반환
    if (memberAccountDto.getRole() == newRoleIdx) {
      return RoleChangeResponseDto.builder()
          .isChanged(false)
          .memberIdx(memberDto.getIdx())
          .email(memberDto.getEmail())
          .id(memberAccountDto.getId())
          .role(roleChangeRequestDto.getToRole())
          .build();
    }

    // 권한 업데이트
    memberAccountDto.setRole(newRoleIdx);
    int updateResult = memberAccountDao.updateRole(memberAccountDto);

    return RoleChangeResponseDto.builder()
        .role(roleChangeRequestDto.getToRole())
        .isChanged(updateResult == 1 ? true : false)
        .memberIdx(memberDto.getIdx())
        .email(memberDto.getEmail())
        .id(memberAccountDto.getId())
        .build();
  }
}
