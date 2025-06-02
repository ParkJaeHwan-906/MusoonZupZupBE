package com.ssafy.musoonzup.member.dto.request;

import com.ssafy.musoonzup.global.entity.BaseEntity;
import java.time.LocalDate;
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
public class RegisterRequestDto {
  private String id;
  private String pw;
  private String name;
  private String email;
  private String phone;
  private Integer gender;
  private LocalDate birthDate;
}

