package com.ssafy.musoonzup.member.dto;

import com.ssafy.musoonzup.global.entity.BaseEntity;
import java.time.LocalDate;
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
public class MemberAccountDto extends BaseEntity {
  private Long idx;
  private Long memberIdx; // FK → Member
  private String id;
  private String pw;
  private Long role;
  private Integer ban;
  private LocalDateTime exitAt;

//  // 필요 시 MemberDto 포함
//  private MemberDto member;
}

