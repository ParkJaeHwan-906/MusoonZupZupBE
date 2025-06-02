package com.ssafy.musoonzup.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.musoonzup.member.dto.MemberRegistPlaceDto;
import com.ssafy.musoonzup.member.dto.response.MemberRegistPlaceListResponseDto;

@Mapper
public interface MemberRegistPlaceDao {
	/*
	 * 사용자 등록 장소를 저장한다. 
	 */
	int insert(MemberRegistPlaceDto memberRegistPlaceDto);
	
	/*
	 * 현재 사용자가 등록해둔 장소의 개수를 반환한다.
	 * 삭제된 장소는 세지 않음 
	 */
	int countRegistPlace(Long memberAccountIdx);
	
	/*
	 * 현재 사용자의 등록 장소를 삭제한다.
	 */
	int delete(Long memberAccountIdx, Long idx);
	
	/*
	 * 현재 사용자의 등록 장소를 조회한다.
	 */
	List<MemberRegistPlaceListResponseDto> selectByMemeberAccountIdx(Long memberAccountIdx);
}
