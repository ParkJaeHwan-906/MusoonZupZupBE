package com.ssafy.musoonzup.admin.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWithAccountDto {
  private Long memberIdx;
  private String id;
  private String email;
  private String name;
  private String role;
  private String gender;  // '0'이면 남성, '1'이면 여성으로 가공
  private boolean isBanned; // 0이면 false, 1이면 ban 된것 true
  private LocalDateTime createdAt;
}