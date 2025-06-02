package com.ssafy.musoonzup.openAi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GptServiceImpl implements GptService {
	private final String basicSetting = "너는 아파트 청약 정보를 분석해주는 전문가야. "
			+ "청약하려는 아파트 정보를 제공할테니 자세하게 분석해줘, 그리고 해당 청약을 추천하는지 추천하지않는지 여부도 알려줘."
			+ "모든 청약을 추천하지 않고, 주변 매매가와 비교하여 조금이라도 저렴한지 비교하여 추천해줘. 주변 매매가보다 비싸다면 더 자세하게 분석해서 추천하는지, 비추천하는지 알려줘 (집 가격이 오를 것 같은지 등의 가능성을 비교)"
			+ "그리고 모든 응답은 영어가 아닌, 한국어를 사용해서 답변해줘.";
//			+ "응답은 마크다운을 절대 사용하지 말고, 일반 Text 형식과 이모티콘만 허용해서 응답해줘";
	
	@Value("${openAI.api.key}")
	String openAiKey;
	
	@Override
	public String callGpt(String userMsg) throws JsonProcessingException {
		final String url = "https://api.openai.com/v1/chat/completions";

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(openAiKey);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		Map<String, Object> bodyMap = new HashMap<>();
//		bodyMap.put("model", "gpt-4.5-preview-2025-02-27");	// 청약 링크 타고 들어가서 문석 가능 
		bodyMap.put("model", "gpt-4o-mini");

		List<Map<String, String>> messages = new ArrayList<>();

		// AI 설정 
		messages.add(Map.of(
		    "role", "system",
		    "content", basicSetting
		));

		// user 요청 추가
		messages.add(Map.of(
		    "role", "user",
		    "content", userMsg
		));

		bodyMap.put("messages", messages);

		
		String body = objectMapper.writeValueAsString(bodyMap);
		
		HttpEntity<String> request = new HttpEntity<>(body, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		JsonNode root = objectMapper.readTree(response.getBody());

		// 응답에서 첫 번째 응답 메시지의 content 꺼내기
		String content = root.path("choices").get(0).path("message").path("content").asText();
		return content;
	}
}
