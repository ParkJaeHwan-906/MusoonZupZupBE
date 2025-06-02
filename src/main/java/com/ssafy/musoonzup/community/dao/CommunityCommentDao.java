package com.ssafy.musoonzup.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.musoonzup.community.dto.CommunityComment;
import com.ssafy.musoonzup.community.dto.response.CommunityCommentList;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

@Mapper
public interface CommunityCommentDao {
	// 댓글 작성 
	int insert(CommunityComment communityComment);
	// 댓글 수정
	int update(CommunityComment communityComment);
	// 댓글 조회 ( 게시글 별 )
	List<CommunityCommentList> selectAllByIdx(Long idx, String role,SearchCondition_TMP searchCondition);
	// 게시글의 댓글 개수 조회
	int selectAllByIdxCnt(Long idx, String role);
	// 댓글 삭제
	int delete(Long idx);
	// 댓글 숨김
	int blind(Long idx, boolean blind);
	// 댓글 조회 ( 댓글 특정 )
	CommunityComment selectByIdx(Long idx);
}
