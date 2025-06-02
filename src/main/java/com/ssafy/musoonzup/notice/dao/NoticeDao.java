package com.ssafy.musoonzup.notice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.notice.dto.NoticeDto;
import com.ssafy.musoonzup.notice.dto.request.NoticeInsertRequestDto;
import com.ssafy.musoonzup.notice.dto.response.NoticeDetailResponseDto;

@Mapper
public interface NoticeDao {
	/*
	 * 공지사항 작성
	 */
	int insert(NoticeInsertRequestDto request, Long memberAccountIdx);
	/*
	 * 공지사항 수정
	 */
	int update(NoticeDto request, Long memberAccountIdx);;
	/*
	 * 공지사항 조회
	 * [Pageable pageable 형식과, 전체 게시글 개수 필요]
	 */
	List<NoticeDetailResponseDto> selectAll(SearchCondition_TMP searchCondition_TMP, String role);
	int selectAllCnt(SearchCondition_TMP searchCondition_TMP, String role);
	/*
	 * 공지사항 상세 조회
	 * blind 값을 거르지 않고, 그대로 준다. 
	 * 프론트에서 처리 
	 */
	NoticeDetailResponseDto selectByIdx(Long idx);
	/*
	 * 공지사항 삭제 ( 복구 불가 )
	 */
	int delete(Long idx, Long memberAccountIdx);
	/*
	 * 공지사항 숨김 처리 ( 복구 가능 )
	 */
	int blind(Long idx, Long memberAccountIdx, Boolean blind);
	
	int updateViews(Long idx);
}
