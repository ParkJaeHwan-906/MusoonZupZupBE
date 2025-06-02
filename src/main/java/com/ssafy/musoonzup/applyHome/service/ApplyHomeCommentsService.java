package com.ssafy.musoonzup.applyHome.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.musoonzup.applyHome.dao.ApplyHomeCommentsDao;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeComments;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeDto;
import com.ssafy.musoonzup.openAi.service.GptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplyHomeCommentsService {
	private final ApplyHomeCommentsDao applyHomeCommentsDao;
	private final GptService gptService;
	
	/*
	 * 해당 공고에 대한 GPT 분석 정보를 가져온다. 
	 * 이때 가장 최신 정보 한 건만 가져온다. 
	 */
	public ApplyHomeComments selectByApplyIdx(Long apply_idx) {
		ApplyHomeComments applyHomeComments = null;
		try {
			applyHomeComments = applyHomeCommentsDao.selectByApplyIdx(apply_idx);
		} catch(DataAccessException e) {
			e.printStackTrace();
		}
		return applyHomeComments;
	}
	
	/*
	 * 해당 공고에대한 GPT 분석을 가져오고,
	 * 이를 DB(apply_home_comments)에 저장한다. 
	 */
	public int insert(ApplyHomeDto applyHomeDto) {
		// 1. GPT 를 사용해 해당 청약 정보를 분석한다. 
		// GPT API 사용 ✅
		String gptComment = null;
		
		try {
			gptComment = gptService.callGpt(makeUserMsg(applyHomeDto));
		} catch (Exception e) {
			e.printStackTrace();
			gptComment = null;
		}
		
		if(gptComment == null) return -1;
		
		int updatedRows = -1;
		// 2. 해당 내용을 DB 에 저장한다. 
		try {
			ApplyHomeComments applyHomeComments = ApplyHomeComments.builder()
					.applyIdx(applyHomeDto.getIdx())
					.comment(gptComment)
					.build();
			
			updatedRows = applyHomeCommentsDao.insert(applyHomeComments);
		} catch (DataAccessException e) {
			e.printStackTrace();
			updatedRows = -1;
		}
		return updatedRows;
	}
	
	
	//------------------------- 내부 메서두 ---------------------------------
	
	/*
	 * GPT 프롬프트 명령어를 작성한다. 
	 */
	public String makeUserMsg(ApplyHomeDto applyHome) {
		StringBuilder prompt = new StringBuilder();
		
		prompt.append(applyHome.getHouseName())
		.append('(').append(applyHome.getHouseAddress()).append(')')
		.append("에 청약을 하려해. 공급 가격은 최대 ").append(applyHome.getSuplyPrice()).append(" 만원으로 공고가 올라왔어. ")
		// url 통해서 상세 정보 분석 -> 표 제공
//		.append(applyHome.getApplyhomeUrl())
//		.append("  해당 링크에 들어가서 여기 링크 들어가서, 공급대상의 정보를 가져올 수 있어? 예를 들어 주택구분,주택형,주택공급면적,(주거전용+주거공용),공급세대수,주택관리번호(모델번호)에서 일반 개수,특별 개수,총계 이런 정보들 분석 해줘. 다음으로는")
		.append("해당 아파트 주변 다른 아파트들은 어떤 아파트가 있는지 그리고 그 아파트들의 평균 매매가를 비교해서 이번 청약이 금액적(매매가보다 조금이라도 저렴한지, 억 단위로 비교해줘)으로 메리트가 있는지 알려줘. ")
		.append("해당 아파트 주변에 편의시설(교육시설, 학교, 대형병원, 대형마트, 편의시설(영화관, 아울렛, 식당 등), 상권 등이 어떻게 분포하고 있는지, 경찰서 소방서 등 각 치안 분야에서는 어떤지 분석해줘")
		.append("만약 해당 청약 정보에 실거주 의무 등 조건이 있다면 해당 조건을 찾아서 알려줘. 그리고 실거주 목적으로는 어떤지, 투자 목적으로는 어떤지도 분석해줘.")
		.append("마지막으로 해당 청약을 추천하는지 안하는지 알려줘");
		
		return prompt.toString();
	}
}
