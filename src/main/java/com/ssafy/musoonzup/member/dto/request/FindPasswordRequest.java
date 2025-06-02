package com.ssafy.musoonzup.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordRequest {
  private String userId;
  private String name;
  private String email;
}
