package com.ssafy.musoonzup.member.dto;

import com.ssafy.musoonzup.global.entity.BaseEntity;
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
public class RoleDto extends BaseEntity {
  private Long idx;
  private String name;
}

