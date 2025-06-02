package com.ssafy.musoonzup.applyHome.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;

import com.ssafy.musoonzup.applyHome.dto.*;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;

@Mapper
public interface ApplyHomeDao {
	List<ApplyHomeDto> selectAll(SearchCondition_TMP searchCondition, String role);
	List<ApplyHomeDto> selectTop3(String sortKey);
	ApplyHomeDto selectByIdx(Long idx);
	int blindPblanc(Long idx, Boolean blind);
	int updateGeo(ApplyHomeDto applyHomeDto);
	int updatedViews(Long idx);
	int selectAllPblancCnt(SearchCondition_TMP searchCondition, String role);
}
