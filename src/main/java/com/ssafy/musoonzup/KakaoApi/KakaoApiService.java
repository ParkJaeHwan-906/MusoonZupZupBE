package com.ssafy.musoonzup.KakaoApi;


import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.musoonzup.applyHome.dto.ApplyHomeDto;

@Service
public class KakaoApiService {
	@Value("${kakao.api.key}")
	String kakaoApiKey;
	
	/*
	 *	주소를 좌표 값으로 변환한다. 
	 */
	public Point searchGeoByAddress(String address) throws JsonMappingException, JsonProcessingException, IllegalAccessException {
		final String url = "https://dapi.kakao.com/v2/local/search/address?query=";
		Point point = null;
		double x = 0.0;
		double y= 0.0;
		try {
			// header 설정 
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "KakaoAK "+kakaoApiKey);	// kakao 전용 접두사 설정 
			
			HttpEntity<String> request = new HttpEntity<>(headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(
					url+address, HttpMethod.GET, request, String.class
					);
			ObjectMapper objectMapper = new ObjectMapper();
			// JSON 문자열을 JsonNode 로 변환 
			JsonNode root = objectMapper.readTree(response.getBody());
			x = root.path("documents").get(0).path("address").path("x").asDouble();
			y = root.path("documents").get(0).path("address").path("y").asDouble();
			
			// 주소로 좌표 값 반환 
//			applyhome.setGeo(new Point(x, y));
		} catch(Exception e) {	// adress 정보로 좌표를 찾지 못했을 경우 -> 키워드로 검색 
//			searchGeoByKeyword(applyhome);
			throw new IllegalAccessException("올바르지 않은 주소 형식");
		}
		return new Point(x, y);
	}
	
	public Map<String, Object> searchGeoByKeyword(String keyWord) throws JsonMappingException, JsonProcessingException {
		final String url = "https://dapi.kakao.com/v2/local/search/keyword?query=";
		String filteredKeyword = keyWord.replaceAll("\\(.*?\\)", "").trim();

		// header 설정 
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "KakaoAK "+kakaoApiKey);	// kakao 전용 접두사 설정 
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				url+filteredKeyword, HttpMethod.GET, request, String.class
				);
		ObjectMapper objectMapper = new ObjectMapper();
		// JSON 문자열을 JsonNode 로 변환 
		JsonNode root = objectMapper.readTree(response.getBody());
		
		String address = root.path("documents").get(0).path("address_name").asText();
		double x = root.path("documents").get(0).path("x").asDouble();
		double y = root.path("documents").get(0).path("y").asDouble();
		return Map.of("Point", new Point(x,y), "address", address);
	}
}
