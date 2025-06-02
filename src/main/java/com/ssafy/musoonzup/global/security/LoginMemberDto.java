package com.ssafy.musoonzup.global.security;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginMemberDto {
  private Long idx;
  private Long memberIdx; // FK → Member
  private String id;
  private String pw;
  private String role;
  private Integer ban;
}
