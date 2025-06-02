package com.ssafy.musoonzup.community.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.musoonzup.applyHome.dto.SearchCondition;
import com.ssafy.musoonzup.community.dao.CommunityDao;
import com.ssafy.musoonzup.community.dto.Community;
import com.ssafy.musoonzup.community.dto.response.CommunityDetail;
import com.ssafy.musoonzup.community.dto.response.CommunityList;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {
	private final CommunityDao communityDao;
	
	// 게시글을 작성한다. 
	public int insert(Community community) throws DataAccessException {
		return communityDao.insert(community);
	}
	
	// 게시글을 수정한다.
	public int update(Community community) throws DataAccessException {
		return communityDao.update(community);
	}
	
	// 게시글을 삭제한다.
	public int delete(Long idx) throws DataAccessException {
		return communityDao.delete(idx);
	}
	
	// 게시글을 숨김처리한다.
	// true : 숨김 처리
	// false : 공개 처리
	public int blind(Long idx, boolean blind) throws DataAccessException {
		return communityDao.blind(idx, blind);
	}
	
	// 게시글을 상세 조회한다.
	// 조회수도 같이 증가한다.
	public CommunityDetail selectByIdx(Long idx) throws DataAccessException {
		communityDao.updateViews(idx);
		return communityDao.selectByIdx(idx);
	}
	
	// 페이징 처리를 위한 전체 게시글 개수 반환 
	public int selectAllCnt(SearchCondition_TMP searchCondition, String role) throws DataAccessException {
		int postCnt = 0;
		try {
			postCnt = communityDao.selectAllCnt(searchCondition, role);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return postCnt;
	}
	
	// 전체 게시글을 조회한다. ( 검색 가능 ) 
	public Page<CommunityList> selectAll(SearchCondition_TMP searchCondition, String role) throws DataAccessException {
		searchCondition = mappingSearchCondition(searchCondition);
		List<CommunityList> communityList = communityDao.selectAll(searchCondition, role);
		Pageable pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		
		return new PageImpl<>(communityList, pageable, selectAllCnt(searchCondition, role));
	}
	
	// 페이징 처리를 위한 나의 전체 게시글 개수 반환 
		public int selectAllByMemberAccountIdxCnt(SearchCondition_TMP searchCondition, Long memberAccountIdx) throws DataAccessException {
			int postCnt = 0;
			try {
				postCnt = communityDao.selectAllByMemberAccountIdxCnt(searchCondition, memberAccountIdx);
			} catch(DataAccessException e) {
				e.printStackTrace();
				throw e;
			}
			return postCnt;
		}
	
	/*
	 * 내가 작성한 게시글 목록을 조회한다.
	 */
	public Page<CommunityList> selectAllByMemberAccountIdx(SearchCondition_TMP searchCondition, Long memberAccountIdx) throws DataAccessException {
		searchCondition = mappingSearchCondition(searchCondition);
//		System.out.println(searchCondition);
		List<CommunityList> communityList = communityDao.selectAllByMemberAccountIdx(searchCondition, memberAccountIdx);
		Pageable pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		
		return new PageImpl<>(communityList, pageable, selectAllByMemberAccountIdxCnt(searchCondition, memberAccountIdx));
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
	 * 2 : 좋아요 순
	 * 3 : 조회수 순
	 * 4 : 좋아요 + 조회수 순
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
				sortKey = "`like`";
				break;
			case "3":
				sortKey = "`views`";
				break;
			case "4":
				sortKey = "`views`+`like`";
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
