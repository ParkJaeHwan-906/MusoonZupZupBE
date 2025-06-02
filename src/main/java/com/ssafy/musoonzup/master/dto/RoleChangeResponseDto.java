package com.ssafy.musoonzup.master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeResponseDto {
  boolean isChanged;
  Long memberIdx;
  String email;
  String id;
  String role;
}
