package com.ssafy.musoonzup.openAi.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/*
 * OpenAI 를 사용하는 메서드를 정의한다. 
 */
public interface GptService {
	String callGpt(String userMsg) throws JsonProcessingException;
}
