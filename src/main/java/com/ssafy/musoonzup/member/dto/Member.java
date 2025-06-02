package com.ssafy.musoonzup.member.dto;

import com.ssafy.musoonzup.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Member extends BaseEntity {
  private int idx;
  private String userId;
  private String pw;
}
