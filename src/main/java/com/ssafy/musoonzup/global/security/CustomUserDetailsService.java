package com.ssafy.musoonzup.global.security;

import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dao.RoleDao;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final RoleDao roleDao;
  private final MemberAccountDao memberAccountDao;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    MemberAccountDto memberAccountDto = memberAccountDao.findById(username)
        .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

    String role = roleDao.findNameByIdx(memberAccountDto.getRole());

    LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                                                  .id(memberAccountDto.getId())
                                                  .pw(memberAccountDto.getPw())
                                                  .memberIdx(memberAccountDto.getMemberIdx())
                                                  .idx(memberAccountDto.getIdx())
                                                  .role(role)
                                                  .ban(memberAccountDto.getBan())
                                                  .build();
    return new CustomUserDetails(loginMemberDto);

  }
}
