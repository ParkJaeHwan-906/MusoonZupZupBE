package com.ssafy.musoonzup.community.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
	private List<T> contents;
	private int pageNo;
	private int pageSize;
	private Long totalElements;
	private int totalPages;
	private boolean last;
}
