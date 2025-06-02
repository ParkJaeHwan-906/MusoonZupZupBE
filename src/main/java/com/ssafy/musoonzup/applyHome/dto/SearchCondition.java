package com.ssafy.musoonzup.applyHome.dto;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 검색 조건을 위한 클래스 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCondition {
	private @Nullable String key;			// 검색 키 
	private @Nullable String value;			// 검색 값
	private @Nullable String sortKey;		// 정렬 키
	private @Nullable String sortValue;		// 정렬 값 (ASC, DESC)
}
