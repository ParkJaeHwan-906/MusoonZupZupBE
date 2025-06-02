package com.ssafy.musoonzup.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "권한 변경 요청 DTO", example = """
{
  "memberAccountIdx": 5,
  "newRole": "ADMIN"
}
""")
public class RoleChangeRequestDto {
  @Schema(description = "변경 대상 계정의 고유 Idx(members_account 테이블의 idx)", example = "5")
  Long memberIdx;
  @Schema(description = "새로운 권한 (예: USER, ADMIN)", example = "ADMIN")
  String toRole;
}
