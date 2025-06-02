package com.ssafy.musoonzup.jwt;

import com.ssafy.musoonzup.global.util.JwtUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class JwtDecodeTest {

  @Test
  public void decodeJwtTest() {
    // 실제 응답으로 받은 accessToken 복사해서 여기에 넣기
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2Nlc3NUb2tlbiIsImRhdGEiOiJGU0E1bzB2RWtGRjdXc1R5MTFJSzhlUTRESkppeDRaTUFjeVJFdW5ib0VNdlZxNTRLajR0SDYwNjI0anhERk5TIiwiaWF0IjoxNzQ1OTg5MzUxLCJleHAiOjE3NDU5OTI5NTF9.zIx3S2vI3qa7zJHI_motYDEm8JgMqSM6GmXOP3X_QVI";

    Map<String, Object> payload = JwtUtil.parseEncryptedPayload(token);
    System.out.println("payload: " + payload);
  }
}