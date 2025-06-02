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
import com.ssafy.musoonzup.global.security.LoginMemberDto;
import com.ssafy.musoonzup.notice.dao.NoticeDao;
import com.ssafy.musoonzup.notice.dto.NoticeDto;
import com.ssafy.musoonzup.notice.dto.request.NoticeInsertRequestDto;
import com.ssafy.musoonzup.notice.dto.response.NoticeDetailResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
	private final NoticeDao noticeDao;
	
	public int insert(NoticeInsertRequestDto request, Long memberAccountIdx) {
		int updatedRows = 0;
		try {
			updatedRows = noticeDao.insert(request, memberAccountIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	
	public int update(NoticeDto request, Long memberAccountIdx) {
		int updatedRows = 0;
		try {
			updatedRows = noticeDao.update(request, memberAccountIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	
	public int selectAllCnt(SearchCondition_TMP searchCondition_TMP, String role) {
		int rowsCnt = 0;
		try {
			rowsCnt = noticeDao.selectAllCnt(searchCondition_TMP, role);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return rowsCnt;
	}
	
	public Page<NoticeDetailResponseDto> selectAll(SearchCondition_TMP searchCondition_TMP, String role) {
		List<NoticeDetailResponseDto> noticeList = null;
		searchCondition_TMP = mappingSearchCondition(searchCondition_TMP);
		Pageable pageable = null;
		try {
			noticeList  = noticeDao.selectAll(searchCondition_TMP, role);
			pageable = PageRequest.of(searchCondition_TMP.getPage(), searchCondition_TMP.getSize());
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return new PageImpl<>(noticeList, pageable, selectAllCnt(searchCondition_TMP, role));
	}
	
	/*
	 * 조회하면서, 조회수를 같이 증가시킨다. 
	 */
	public NoticeDetailResponseDto selectByIdx(Long idx) {
		NoticeDetailResponseDto res = null;
		try {
			noticeDao.updateViews(idx);
			res = noticeDao.selectByIdx(idx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return res;
	}
	
	/*
	 * 삭제 요청 권한을 확인 후, 삭제한다.
	 */
	public int delete(Long idx, LoginMemberDto loginUser) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			// 관리자가 요청한게 아니라면
			if(!loginUser.getRole().toUpperCase().equals("ADMIN") && !loginUser.getRole().toUpperCase().equals("MASTER")) {
				throw new IllegalAccessException("접근 권한이 없습니다.");
			}
			
			updatedRows = noticeDao.delete(idx, loginUser.getIdx());
		} catch(DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
	
	/*
	 * 숨김 / 공개 요청 권한을 확인 후, 삭제한다.
	 * [true : 숨김, false : 공개]
	 */
	public int blind(Long idx, LoginMemberDto loginUser, Boolean blind) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			// 관리자가 요청한게 아니라면
			if(!loginUser.getRole().toUpperCase().equals("ADMIN") && !loginUser.getRole().toUpperCase().equals("MASTER")) {
				throw new IllegalAccessException("접근 권한이 없습니다.");
			}
			
			updatedRows = noticeDao.blind(idx, loginUser.getIdx(), blind);
		} catch(DataAccessException e) {
			throw e;
		}
		return updatedRows;
	}
	
	/*
	 * Request 로 들어온 SeachCondition 을 DB 내의 Column 명으로 변환한다. 
	 * 
	 * [searchKey]
	 * 1 : 게시글 제목 ( title )
	 * 2 : 게시글 내용 ( content )
	 * 
	 * [sortKey] 
	 * 1 : 작성일 순
	 * 2 : 조회수 순
	 */
	private SearchCondition_TMP mappingSearchCondition(SearchCondition_TMP searchCondition) {
		if(searchCondition.getKey() != null) {
			String searchKey = searchCondition.getKey();
			
			switch (searchKey) {
			case "1":
				searchKey = "title";
				break;
			case "2": 
				searchKey = "content";
				break;
			default:
				searchKey = null;
			}
			
			searchCondition.setKey(searchKey);
		}
		if(searchCondition.getSortKey() != null) {
			String sortKey = searchCondition.getSortKey();
			String sortValue = searchCondition.getSortValue();
			switch (sortKey) {
			case "1":
				sortKey = "`created_at`";
				break;
			case "2":
				sortKey = "`views`";
				break;
			default:
				sortKey = null;
			}
			
			searchCondition.setSortKey(sortKey);
			searchCondition.setSortValue(resolveSortValue(sortValue));
		}
		// 페이징 처리
		searchCondition.setOffset(searchCondition.getPage()*searchCondition.getSize());
		return searchCondition;
	}
	
	private String resolveSortValue(String sortValue) {
	    return "ASC".equalsIgnoreCase(sortValue) ? "ASC" : "DESC";
	  }
}
