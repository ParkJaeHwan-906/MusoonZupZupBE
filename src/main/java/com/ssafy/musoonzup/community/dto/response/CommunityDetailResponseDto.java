package com.ssafy.musoonzup.community.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDetailResponseDto {
	private Long idx;
    private Long userAccountIdx;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 댓글 목록 포함
    private List<CommunityCommentResponseDto> comments;
}
