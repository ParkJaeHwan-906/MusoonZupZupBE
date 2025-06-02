package com.ssafy.musoonzup.notice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.notice.dto.request.NoticeCommentRequestDao;
import com.ssafy.musoonzup.notice.dto.response.NoticeCommentResponseDto;

@Mapper
public interface NoticeCommentDao {
	/*
	 * 댓글 작성
	 * [작성자, 공지사항, 내용]
	 */
	int insert(NoticeCommentRequestDao req);
	/*
	 * 댓글 조회 ( 게시글 별 )
	 * [관리자라면 blind 처리된 댓글 목록 / 사용자라면 blind 처리 되지 않은 댓글 목록]
	 */
	List<NoticeCommentResponseDto> selectByIdx(SearchCondition_TMP searchCondition, Long noticeIdx, String role);
	int selectByIdxCnt(SearchCondition_TMP searchCondition, Long noticeIdx, String role);
	/*
	 * 댓글 삭제
	 */
	int delete(Long idx, Long memberAccountIdx);
	/*
	 * 댓글 수정
	 */
	int update(Long idx, NoticeCommentRequestDao req);
	/*
	 * 댓글 숨김/공개 처리
	 */
	int blind(Long idx, Long memberAccountIdx, Boolean blind);
}