package com.ssafy.musoonzup.global.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {
  private static final String SECRET = "jwtsecretjwtsecretjwtsecretjwtsecret"; // 32자 이상
  private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String createAccessToken(Long memberAccountIdx, Long memberIdx, String id, Long role) {
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("memberAccountIdx", memberAccountIdx);
      payload.put("memberIdx", memberIdx);
      payload.put("id", id);
      payload.put("role", role);

      String json = objectMapper.writeValueAsString(payload);
      String encrypted = JwtAesUtil.encrypt(json);

      return Jwts.builder()
          .setSubject("accessToken")
          .claim("data", encrypted)
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1시간
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
    } catch (Exception e) {
      throw new RuntimeException("JWT(access) 생성 실패", e);
    }
  }

  public static String createRefreshToken(Long memberAccountIdx) {
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("memberAccountIdx", memberAccountIdx);
      String json = objectMapper.writeValueAsString(payload);
      String encrypted = JwtAesUtil.encrypt(json);

      return Jwts.builder()
          .setSubject("refreshToken")
          .claim("data", encrypted)
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7일
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
    } catch (Exception e) {
      throw new RuntimeException("JWT(refresh) 생성 실패", e);
    }
  }

  public static Map<String, Object> parseEncryptedPayload(String token) {
    try {
      String encrypted = Jwts.parserBuilder().setSigningKey(key).build()
          .parseClaimsJws(token).getBody().get("data", String.class);

      String decryptedJson = JwtAesUtil.decrypt(encrypted);
      return objectMapper.readValue(decryptedJson, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }
}

