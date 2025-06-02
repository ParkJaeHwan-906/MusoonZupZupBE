package com.ssafy.musoonzup.TmapApi.service;

import java.util.Collections;
import java.util.HashMap;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.musoonzup.TmapApi.dto.request.FindCarRouteRequestDto;

@Service
public class TMapService {
	@Value("${tMap.api.key}")
	private String tMapApiKey;
	
	// 자동차 경로
	public Object searchNavRoute(FindCarRouteRequestDto req) throws JsonMappingException, JsonProcessingException{
		final String url = "https://apis.openapi.sk.com/tmap/routes?version=1";

		Map<String, Object> body = new HashMap<>();
		body.put("startX", req.getStartX());
		body.put("startY", req.getStartY());
		body.put("endX", req.getEndX());
		body.put("endY", req.getEndY());
		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("appKey", tMapApiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
		    url,
		    HttpMethod.POST,
		    request,
		    String.class
		);

		ObjectMapper objectMapper = new ObjectMapper();
		// JSON 문자열을 JsonNode 로 변환 
		JsonNode root = objectMapper.readTree(response.getBody());
		
		return root;
	}
	// 대중교통 경로
	public Object searchTransitRoute(FindCarRouteRequestDto req) throws JsonMappingException, JsonProcessingException{
		final String url = "https://apis.openapi.sk.com/transit/routes";

		Map<String, Object> body = new HashMap<>();
		body.put("startX", req.getStartX());
		body.put("startY", req.getStartY());
		body.put("endX", req.getEndX());
		body.put("endY", req.getEndY());
		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("appKey", tMapApiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
		    url,
		    HttpMethod.POST,
		    request,
		    String.class
		);
		ObjectMapper objectMapper = new ObjectMapper();
		// JSON 문자열을 JsonNode 로 변환 
		JsonNode root = objectMapper.readTree(response.getBody());
		
		return root;
	}
	// 보행자 경로
	public Object searchPedestrianNavRoute(FindCarRouteRequestDto req) throws JsonMappingException, JsonProcessingException{
		final String url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1";

		Map<String, Object> body = new HashMap<>();
		body.put("startName", req.getStartPlaceAddress());
		body.put("startX", req.getStartX());
		body.put("startY", req.getStartY());
		body.put("endName", req.getEndPlaceAddress());
		body.put("endX", req.getEndX());
		body.put("endY", req.getEndY());
		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("appKey", tMapApiKey);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
		    url,
		    HttpMethod.POST,
		    request,
		    String.class
		);

		ObjectMapper objectMapper = new ObjectMapper();
		// JSON 문자열을 JsonNode 로 변환 
		JsonNode root = objectMapper.readTree(response.getBody());
		
		return root;
	}
}
