package com.ssafy.musoonzup.community.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.musoonzup.community.dao.CommunityLike_DisLikeDao;
import com.ssafy.musoonzup.community.dto.response.CommunityLikeDisLikeResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityLike_DisLikeService {
	private final CommunityLike_DisLikeDao communityLike_DisLikeDao;

	/*
	 * 좋아요 혹은 싫어요를 눌렀는지에 대한 검증 
	 * -> 게시글마다 회원 상태 표시도 ?
	 */
	public Integer checked(Long memberAccountIdx, Long communityIdx) throws DataAccessException, IllegalAccessException {
		Integer likeFlag = null;
		try {
			likeFlag = communityLike_DisLikeDao.checked(memberAccountIdx, communityIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalAccessException("예상치 못한 오류가 발생하였습니다.");
		}
		return likeFlag;
	}
	/*
	 * 좋아요 / 싫어요 누르기
	 * likeFlag 값에 따라 구분
	 * 0 : 싫어요 
	 * 1 : 좋아요 
	 */
	public Integer registLikeOrDisLike(Long memberAccountIdx, Long communityIdx, Long likeFlag) throws DataAccessException, IllegalAccessException {
		if(checked(memberAccountIdx, communityIdx) != null) throw new IllegalAccessException("이미 등록되어 있습니다.");
		int updatedRows = 0;
		try {
			updatedRows = communityLike_DisLikeDao.registLikeOrDisLike(memberAccountIdx, communityIdx, likeFlag);
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return updatedRows;
	}
	/*
	 * 좋아요 / 싫어요 취소 
	 */
	public Integer cancelLikeOrDisLike(Long memberAccountIdx, Long communityIdx) throws DataAccessException, IllegalAccessException {
		if(checked(memberAccountIdx, communityIdx) == null) throw new IllegalAccessException("등록 내역이 없습니다.");
		int updatedRows = 0;
		try {
			updatedRows = communityLike_DisLikeDao.cancelLikeOrDisLike(memberAccountIdx, communityIdx);
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	/*
	 * 게시글의 좋아요 / 싫어요 개수 반환
	 */
	public CommunityLikeDisLikeResponseDto cntLikeDisLikeByIdx(Long communityIdx) {
		CommunityLikeDisLikeResponseDto communityLikeDisLikeResponseDto = null;
		try {
			communityLikeDisLikeResponseDto = communityLike_DisLikeDao.cntLikeDisLikeByIdx(communityIdx);
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return communityLikeDisLikeResponseDto;
	}
}