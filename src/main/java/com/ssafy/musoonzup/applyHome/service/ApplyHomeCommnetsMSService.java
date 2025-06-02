package com.ssafy.musoonzup.applyHome.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ssafy.musoonzup.applyHome.dao.ApplyHomeCommentsMSDao;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeCommentsMS;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeDto;
import com.ssafy.musoonzup.openAi.service.GptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplyHomeCommnetsMSService {

    private final ApplyHomeService applyHomeService;

    private final ApplyHomeCommentsService applyHomeCommentsService;
	private final ApplyHomeCommentsMSDao applyHomeCommentsMSDao;
	
	private final GptService gptService;
	
	/*
	 * 사용자가 이전에 검색했던 기록을 불러온다. 
	 */
	public List<ApplyHomeCommentsMS> selectAllComments(Long member_account_idx, Long apply_idx) {
		List<ApplyHomeCommentsMS> allCommnets = null;
		try {
			allCommnets = applyHomeCommentsMSDao.selectAllComments(member_account_idx, apply_idx);
		} catch(DataAccessException e) {
			e.printStackTrace();
			allCommnets = null;
		}
		return allCommnets;
	}
	
	/*
	 * 현재 사용자가 현재 공고에 대해 검색한 결과를 저장한다. 
	 */
	public int insert(ApplyHomeCommentsMS applyHomeCommentsMS) {
		int updatedRows = -1;
		try {
			updatedRows = applyHomeCommentsMSDao.insert(applyHomeCommentsMS);
		} catch(DataAccessException e) {
			e.printStackTrace();
			updatedRows = -1;
		}
		
		return updatedRows;
	}
	
	/*
	 * 사용자에 질문에 대한 분석 결과를 GPT 를 이용해 반환한다.
	 */
	public String getGptComment(ApplyHomeDto applyHome, String request, String userName) {
		// 해당 청약에 대한 기본 Prompt 를 생성한다.
		StringBuilder prompt = new StringBuilder(applyHomeCommentsService.makeUserMsg(applyHome));
		prompt.append("지금부터는 개인적인 나의 상황을 대입한 질문이야. 이전까지의 질문을 통해 기본적인 분석 이후, 나의 질문, 상황 등을 고려하여 청햑 정보를 분석해줘.")
		.append("분석은 이전 질문을 기반으로, 추가적인 나의 질문에 대한 답도 제공해줘. 그리고 역시 이런 모든 상황을 고려할 때, 이번 청약을 추천하는지 하지 않는지 솔직하게 대답해줘.")
		.append("그리고 내 이름은 ").append(userName).append(" 이야. 만약 나를 부른다면 이름으로 불러줘.")
		.append("아래는 나의 질문이야.").append('\n').append(request);
		
		String gptComment = null;
		try {
			gptComment = gptService.callGpt(prompt.toString());
		} catch(Exception e) {
			e.printStackTrace();
			gptComment = null;
		}
		
		return gptComment;
	}
}
