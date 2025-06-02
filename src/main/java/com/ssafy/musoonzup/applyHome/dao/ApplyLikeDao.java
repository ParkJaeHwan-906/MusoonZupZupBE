package com.ssafy.musoonzup.applyHome.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.musoonzup.applyHome.dto.ApplyLikeDto;
import com.ssafy.musoonzup.applyHome.dto.response.ApplyLikeListResponseDto;

@Mapper
public interface ApplyLikeDao {
	/*
	 * 찜 추가 
	 */
	int insert(ApplyLikeDto applyLikeDto);
	/*
	 * 찜 삭제 
	 */
	int delete(Long memberAccountIdx, Long applyIdx);
	/*
	 * 찜 목록 조회
	 */
	List<ApplyLikeListResponseDto> selectAll(Long memberAccountIdx);
	/*
	 * 찜 목록 조회 페이징 처리를 위한 개수 조회
	 */
	int selectAllCnt(Long memberAccountIdx);
	/*
	 * 찜 내역 조회
	 * (중복 방지)
	 */
	int selectByAIdxAndmIdx(Long memberAccountIdx, Long applyIdx);
}
