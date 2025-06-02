package com.ssafy.musoonzup.global.searchCondition;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 검색 조건 및 페이징을 위한 임시 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCondition_TMP {
  private @Nullable String key;			// 검색 키
  private @Nullable String value;			// 검색 값
  private @Nullable String sortKey;		// 정렬 키
  private @Nullable String sortValue;		// 정렬 값 (ASC, DESC)
  private Integer page = 0;  // 0-based
  private Integer size = 10;
  private Integer offset;
}

