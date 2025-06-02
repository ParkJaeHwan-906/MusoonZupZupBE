package com.ssafy.musoonzup.community.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.musoonzup.community.dao.CommunityCommentDao;
import com.ssafy.musoonzup.community.dto.CommunityComment;
import com.ssafy.musoonzup.community.dto.response.CommunityCommentList;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {
	private final CommunityCommentDao communityCommentDao;
	
	// 댓글을 작성한다.
	public int insert(CommunityComment communityComment) throws DataAccessException {
		return communityCommentDao.insert(communityComment);
	}
	
	// 댓글을 수정한다.
	public int update(CommunityComment communityComment) throws DataAccessException {
		return communityCommentDao.update(communityComment);
	}
	
	// 댓글을 삭제한다.
	public int delete(Long idx) throws DataAccessException {
		return communityCommentDao.delete(idx);
	}
	
	// 댓글을 숨긴다.
	// true : 숨김 처리
	// false : 공개 처리
	public int blind(Long idx, boolean blind) throws DataAccessException {
		return communityCommentDao.blind(idx, blind);
	}
	
	// 해당하는 게시글의 댓글을 모두 가져온다. 
	public Page<CommunityCommentList> selectAllByIdx(Long idx, String role, SearchCondition_TMP searchCondition) {
		searchCondition.setOffset(searchCondition.getPage()*searchCondition.getSize());
		List<CommunityCommentList> allComments = communityCommentDao.selectAllByIdx(idx, role, searchCondition);
		Pageable pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		return new PageImpl<>(allComments, pageable, selectAllByIdxCnt(idx, role));	
	}
	// 해당 게시글의 모든 댓글 개수를 가져온다.
	public int selectAllByIdxCnt(Long idx, String role) {
		return communityCommentDao.selectAllByIdxCnt(idx, role);
	}
	// 특정 댓글을 조회한다.
	public CommunityComment selectByIdx(Long idx) throws DataAccessException {
		return communityCommentDao.selectByIdx(idx);
	}
}
