package com.ssafy.musoonzup.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeInsertRequestDto {
	/*
	 * 공지사항 작성을 위한 요청
	 * 제목과 내용을 입력 받음 
	 */
	private String title;
	private String content;
}
