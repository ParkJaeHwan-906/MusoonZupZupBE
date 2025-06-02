package com.ssafy.musoonzup.applyHome.service;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ssafy.musoonzup.KakaoApi.KakaoApiService;
import com.ssafy.musoonzup.applyHome.dao.ApplyHomeDao;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeDto;
import com.ssafy.musoonzup.applyHome.dto.SearchCondition;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyHomeService {
	private final ApplyHomeDao applyHomeDao;
	private final KakaoApiService kService;
	
	/*
	 * 페이징 처리를 위한 전체 공고 개수 조회
	 */
	private int selectAllPblancCnt(SearchCondition_TMP searchCondition, String role) throws DataAccessException {
		int totalPblancCnt = 0;
		try {
			totalPblancCnt = applyHomeDao.selectAllPblancCnt(searchCondition, role);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return totalPblancCnt;
	}
	
	public Page<ApplyHomeDto> selectAllApply(SearchCondition_TMP searchCondition, String role) {
		// 검색 조건 완성 
		searchCondition = mappingColumn(searchCondition);
		
		List<ApplyHomeDto> applyHomeList = applyHomeDao.selectAll(searchCondition, role);
		Pageable pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize());
		return new PageImpl<>(applyHomeList, pageable, selectAllPblancCnt(searchCondition, role));
	}
	
	public List<ApplyHomeDto> selectTop3Apply(String sortKey) {
		if (sortKey.equals("pblanc_date") || sortKey.equals("pblancDate")) sortKey = "pblanc_date";
		else if (sortKey.equals("views") || sortKey.equals("view")) sortKey = "views";
		else throw new IllegalArgumentException("올바르지 못한 정렬 키 값입니다.");
		
		return applyHomeDao.selectTop3(sortKey);
	}
	
	/*
	 * Request 로 들어온 SeachCondition 을 DB 내의 Column 명으로 변환한다. 
	 * 
	 * [searchKey]
	 * 1 : 주택 관리 번호
	 * 2 : 공고 번호
	 * 3 : 지역
	 * 4 : 주택명
	 * 
	 * [sortKey]
	 * 1 : 가격 순 
	 * 2 : 공고 날짜 순
	 * 3 : 청약 시작일 순 
	 * 4 : 청약 종료일 순
	 * 5 : 청약 당첨자 발표 순
	 */
	private SearchCondition_TMP mappingColumn(SearchCondition_TMP searchCondition) {
		// 검색 키워드 처리
		if(searchCondition.getKey() != null) {
			String searchKey = searchCondition.getKey();
			switch (searchKey) {
			case "1": 
				searchKey = "house_manage_no";
				break;
			case "2": 
				searchKey = "pblanc_no";
				break;
			case "3": 
				searchKey = "house_address";
				break;
			case "4": 
				searchKey = "house_name";
				break;
			default :
				searchKey = null;
			}
			searchCondition.setKey(searchKey);
		}
		// 정렬 조건 처리
		if(searchCondition.getSortKey() != null) {
			String sortKey = searchCondition.getSortKey();
			String sortValue = searchCondition.getSortValue();
			switch (sortKey) {
			case "1": 
				sortKey = "suply_price";
				break;
			case "2": 
				sortKey = "pblanc_date";
				break;
			case "3": 
				sortKey = "apply_start_date";
				break;
			case "4": 
				sortKey = "apply_end_date";
				break;
			case "5": 
				sortKey = "apply_announce_date";
				break;
			default :
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
	
	/*
	 * 해당 청약의 상세 정보를 불러온다.
	 * 
	 * 
	 * GPT 에 해당 청약 정보 분석을 요청한다. ✅ 
	 */
	public ApplyHomeDto selectByIdx(Long idx) {
		applyHomeDao.updatedViews(idx);
		return applyHomeDao.selectByIdx(idx);
	}
	
	/*
	 * 청약 정보를 숨김처리/공개처리 한다.
	 */
	public int blindPblanc(Long idx, Boolean blind) {
		int updatedRows;
		try {
			updatedRows = applyHomeDao.blindPblanc(idx, blind);
		} catch(DataAccessException e) {
			e.printStackTrace();
			updatedRows=-1;
		}
		return updatedRows;
	}
	
	/*
	 * 청약 정보의 houseAddress 를 사용하여, 좌표 정보를 호출 후, 저장한다. 
	 */
	public ApplyHomeDto searchGeo(ApplyHomeDto applyHome) {
		String address = applyHome.getHouseAddress();
		try {
		// 외부 API 연동하여 좌표 정보 불러오기 ✅
		// applyHome 의 geo 에 매핑하기 ✅
		applyHome.setGeo(kService.searchGeoByAddress(applyHome.getHouseAddress()));
		applyHomeDao.updateGeo(applyHome);
		// DB apply_home 테이블에 해당 정보 update 해주기 ✅ 
		} catch(IllegalAccessException e) {	// 주소 변환에 실패
			Map<String, Object> res = null;
			try {
				res = kService.searchGeoByKeyword(applyHome.getHouseName());
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
			applyHome.setGeo((Point) res.get("Point"));
			applyHome.setHouseAddress((String)res.get("address"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return applyHome;
	}
}
