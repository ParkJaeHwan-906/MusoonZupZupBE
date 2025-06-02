package com.ssafy.musoonzup.community.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.musoonzup.community.dto.response.CommunityLikeDisLikeResponseDto;

@Mapper
public interface CommunityLike_DisLikeDao {
	// 좋아요/싫어요 선택 여부
	Integer checked(Long memberAccountIdx, Long communityIdx);

	// 좋아요 / 싫어요
	/*
	 * 1 : 좋아요
	 * 0 : 싫어요
	 */
	// 등록
	int registLikeOrDisLike(Long memberAccountIdx, Long communityIdx, Long likeFlag);
	// 취소
	int cancelLikeOrDisLike(Long memberAccountIdx, Long communityIdx);
	// 게시글의 좋아요 / 싫어요 개수를 조회
	CommunityLikeDisLikeResponseDto cntLikeDisLikeByIdx(Long communityIdx);
}