package com.ssafy.musoonzup.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.ssafy.musoonzup.KakaoApi.KakaoApiService;
import com.ssafy.musoonzup.member.dao.MemberRegistPlaceDao;
import com.ssafy.musoonzup.member.dto.MemberRegistPlaceDto;
import com.ssafy.musoonzup.member.dto.response.MemberRegistPlaceListResponseDto;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberRegistPlaceService {

	private final MemberRegistPlaceDao memberRegistPlaceDao;
	private final KakaoApiService kakaoApiService;
	
	/*
	 * 사용자 등록 장소를 저장한다. 
	 * 
	 * 전달받은 정보를 바탕으로 KaKao API 를 활용해 좌표 정보를 불러온다.
	 */
	public int insert(MemberRegistPlaceDto memberRegistPlaceDto) throws IllegalAccessException, DataAccessException {
		int updatedRows = 0;
		try {
			Point geo = kakaoApiService.searchGeoByAddress(memberRegistPlaceDto.getAddress());
			memberRegistPlaceDto.setGeo(geo);
			
			// 별칭이 없으면 주소로 저장 
			if(memberRegistPlaceDto.getAlias() == null) memberRegistPlaceDto.setAlias(memberRegistPlaceDto.getAddress());
			
//			System.out.println(memberRegistPlaceDto.toString());
			
			updatedRows = memberRegistPlaceDao.insert(memberRegistPlaceDto);
		} catch(DataAccessException e) { // MyBatis 오류 
			e.printStackTrace();
			throw e;
		} catch(Exception e) {	// 기타 예외, API 호출
			e.printStackTrace();
			throw new IllegalAccessException("API Error");
		}
		return updatedRows;
	}
	
	/*
	 * 현재 사용자가 등록한 장소의 개수를 반환한다. 
	 * 삭제된 장소는 제외
	 */
	public Integer getRegistCnt(Long memberAccountIdx) {
		Integer registCnt = null;
		try {
			registCnt = memberRegistPlaceDao.countRegistPlace(memberAccountIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return registCnt;
	}
	
	/*
	 * 현재 사용자의 등록한 장소를 삭제한다
	 */
	public Integer deletePlace(Long memberAccountIdx, Long idx) {
		Integer updatedRows = null;
		try {
			updatedRows = memberRegistPlaceDao.delete(memberAccountIdx, idx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return updatedRows;
	}
	
	/*
	 * 현재 사용자의 등록 장소 목록을 조회한다.
	 */
	public List<MemberRegistPlaceListResponseDto> selectByMemeberAccountIdx(Long memberAccountIdx) {
		List<MemberRegistPlaceListResponseDto> registPlace = null;
		try {
			registPlace = memberRegistPlaceDao.selectByMemeberAccountIdx(memberAccountIdx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		}
		return registPlace;
	}
}
