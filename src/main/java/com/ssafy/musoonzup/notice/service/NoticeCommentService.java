package com.ssafy.musoonzup.notice.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.notice.dao.NoticeCommentDao;
import com.ssafy.musoonzup.notice.dto.request.NoticeCommentRequestDao;
import com.ssafy.musoonzup.notice.dto.response.NoticeCommentResponseDto;
import com.ssafy.musoonzup.notice.dto.response.NoticeDetailResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommentService {
	private final NoticeCommentDao commentDao;
	
	/*
	 * 댓글을 작성한다.
	 */
	public int insert(Long noticeIdx, Long memberAccountIdx, NoticeCommentRequestDao req) {
		int updatedRows = 0;
		try {
			// 부족한 정보 보충 
			req.setMemberAccountIdx(memberAccountIdx);
			req.setNoticeIdx(noticeIdx);
			updatedRows = commentDao.insert(req);
		} catch (DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
	
	/*
	 * 댓글을 조회한다. 
	 */
	public Page<NoticeCommentResponseDto> selectByIdx(Long noticeIdx, String role, SearchCondition_TMP searchCondition) {
		List<NoticeCommentResponseDto> noticeCommentList = null;
		Pageable pageable = null;
		try {
			// 페이징 처리
			searchCondition.setOffset(searchCondition.getPage()*searchCondition.getSize());
			noticeCommentList  = commentDao.selectByIdx(searchCondition, noticeIdx, role);
			pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return new PageImpl<>(noticeCommentList, pageable, selectByIdxCnt(noticeIdx, role, searchCondition));
	}
	
	private int selectByIdxCnt(Long noticeIdx, String role, SearchCondition_TMP searchCondition) {
		int selectedRows = 0;
		try {
			// 페이징 처리
			searchCondition.setOffset(searchCondition.getPage()*searchCondition.getSize());
			selectedRows = commentDao.selectByIdxCnt(searchCondition, noticeIdx, role);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return selectedRows;
	}
	
	public int delete(Long idx, Long memberAccountIdx) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			updatedRows = commentDao.delete(idx, memberAccountIdx);
			if(updatedRows == 0) throw new IllegalAccessException("잘못된 접근입니다.");
		} catch (DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
	
	public int update(Long idx, NoticeCommentRequestDao req) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			updatedRows = commentDao.update(idx, req);
			if(updatedRows == 0) throw new IllegalAccessException("잘못된 접근입니다.");
		} catch (DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
	
	public int blind(Long idx, Long memberAccountIdx, Boolean blind) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			updatedRows = commentDao.blind(idx, memberAccountIdx, blind);
			if(updatedRows == 0) throw new IllegalAccessException("잘못된 접근입니다.");
		} catch (DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
}
