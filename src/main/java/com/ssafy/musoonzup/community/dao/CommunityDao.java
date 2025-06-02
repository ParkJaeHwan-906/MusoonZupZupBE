package com.ssafy.musoonzup.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;

import com.ssafy.musoonzup.applyHome.dto.SearchCondition;
import com.ssafy.musoonzup.community.dto.Community;
import com.ssafy.musoonzup.community.dto.response.CommunityDetail;
import com.ssafy.musoonzup.community.dto.response.CommunityList;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

@Mapper
public interface CommunityDao {
	// 게시글 작성
	int insert(Community community);
	// 게시글 수정 
	int update(Community community);
	// 게시글 삭제 ( 사용자 ) 
	int delete(Long idx);
	// 게시글 숨김 ( 관리자 )
	int blind(Long idx, boolean blind);
	// 게시글 상세 조회 
	CommunityDetail selectByIdx(Long idx);
	// 전체 게시글 조회 ( 검색 가능 )
	List<CommunityList> selectAll(SearchCondition_TMP searchCondition, String role);
	// 전체 게시글 개수 조회
	int selectAllCnt(SearchCondition_TMP searchCondition, String role);
	// 조회수 증가
	int updateViews(Long idx);
	// 내가 쓴 전체 게시글 조회 ( 검색 가능 )
	List<CommunityList> selectAllByMemberAccountIdx(SearchCondition_TMP searchCondition, Long memberAccountIdx);
	// 내가 쓴 전체 게시글 개수 조회
	int selectAllByMemberAccountIdxCnt(SearchCondition_TMP searchCondition, Long memberAccountIdx);
	
}
