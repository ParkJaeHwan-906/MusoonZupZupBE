package com.ssafy.musoonzup.applyHome.dao;

import com.ssafy.musoonzup.applyHome.dto.ApplyHomeCommentsMS;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplyHomeCommentsMSDao {
	/*
	 * 이전에 검색한 GPT 분석을 불러온다.
	 */
	List<ApplyHomeCommentsMS> selectAllComments(Long memberAccountIdx, Long applyIdx);
	/*
	 * 현재 사용자가 검색한 분석 결과를 DB 에 저장한다. 
	 */
	int insert(ApplyHomeCommentsMS applyHomeCommentsMS);
}
