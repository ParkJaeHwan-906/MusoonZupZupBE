package com.ssafy.musoonzup.applyHome.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.musoonzup.applyHome.dao.ApplyLikeDao;
import com.ssafy.musoonzup.applyHome.dto.ApplyLikeDto;
import com.ssafy.musoonzup.applyHome.dto.response.ApplyLikeListResponseDto;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyLikeService {
	private final ApplyLikeDao applyLikeDao;
	
	/*
	 * 찜 정보를 추가한다.
	 * [ApplyLikeDto]
	 */
	public int insert(ApplyLikeDto applyLikeDto) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			if(applyLikeDao.selectByAIdxAndmIdx(applyLikeDto.getMemberAccountIdx(), applyLikeDto.getApplyIdx()) > 0) {	// 이미 같은 찜 내역이 존재
				throw new IllegalAccessException("동일 내역이 이미 존재합니다.");
			}
			updatedRows = applyLikeDao.insert(applyLikeDto);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	/*
	 * 침 정보를 삭제한다.
	 * [idx, applyIdx, memberAccount]
	 */
	public int delete(Long memberAccountIdx, Long applyIdx) throws IllegalAccessException {
		int updatedRows = 0;
		try {
			if(applyLikeDao.selectByAIdxAndmIdx(memberAccountIdx, applyIdx) == 0) {	// 삭제할 내역이 없음 
				throw new IllegalAccessException("삭제할 내역이 없습니다.");
			}
			updatedRows = applyLikeDao.delete(memberAccountIdx, applyIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	/*
	 * 페이징 처리를 위한 현 사용자의 찜 개수 조회
	 */
	private int selectAllCnt(Long memberAccountIdx) {
		int likeCnt = 0;
		try {
			likeCnt = applyLikeDao.selectAllCnt(memberAccountIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return likeCnt;
	}
	/*
	 * 찜 정보를 조회한다.
	 * [memberAccountIdx]
	 */
	public Page<ApplyLikeListResponseDto> selectAll(Long memberAccountIdx, SearchCondition_TMP searchCondition) {
		List<ApplyLikeListResponseDto> list = null;
		Pageable pageable;
		try {
			list = applyLikeDao.selectAll(memberAccountIdx);
			pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return new PageImpl<>(list, pageable, selectAllCnt(memberAccountIdx));
	}
	
	/*
	 * 사용자의 현 공고에 대한 찜 정보를 조회한다. 
	 */
	public int selectByAIdxAndmIdx(Long memberAccountIdx, Long applyIdx) {
		int likeResult = 0;
		try {
			likeResult = applyLikeDao.selectByAIdxAndmIdx(memberAccountIdx, applyIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return likeResult;
	}
}
