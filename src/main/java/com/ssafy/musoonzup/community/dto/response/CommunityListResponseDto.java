package com.ssafy.musoonzup.community.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityListResponseDto {
	private Long idx;
    private Long userAccountIdx;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 댓글 수만 포함
    private Integer commentCount;
}
