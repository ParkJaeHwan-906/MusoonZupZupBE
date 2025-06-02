package com.ssafy.musoonzup.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponseDto {
  private String name;
  private String email;
  private String id;
  private String gender; // 0: "남성" 1: "여성"
  private String role; 
}