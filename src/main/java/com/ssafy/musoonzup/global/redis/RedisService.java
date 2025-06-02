package com.ssafy.musoonzup.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
  private final StringRedisTemplate redisTemplate;

  public void set(String key, String value, Duration duration) {
    redisTemplate.opsForValue().set(key, value, duration);
  }

  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  public void setRefreshToken(Long memberAccountIdx, String token, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set("refresh:" + memberAccountIdx, token, timeout, unit);
  }


  public String getRefreshToken(Long memberAccountIdx) {
    return redisTemplate.opsForValue().get("refresh:" + memberAccountIdx);
  }

  public void deleteRefreshToken(Long memberAccountIdx) {
    redisTemplate.delete("refresh:" + memberAccountIdx);
  }

  public boolean hasRefreshToken(Long memberIdx) {
    return redisTemplate.hasKey("refresh:" + memberIdx);
  }
}
