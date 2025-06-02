//package com.ssafy.musoonzup.openAi.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.ssafy.musoonzup.openAi.service.GptService;
//import com.ssafy.musoonzup.openAi.service.GptServiceImpl;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/openai")
//public class GptController {
//	private final GptService gptService;
//	
//	@GetMapping("/search")
//	public String callGpt() {
//		try {
//			System.out.println(gptService.callGpt(""));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return "하이~";
//	}
//}
