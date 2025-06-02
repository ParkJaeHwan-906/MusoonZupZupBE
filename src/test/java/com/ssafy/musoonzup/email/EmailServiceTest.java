package com.ssafy.musoonzup.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.musoonzup.global.email.service.EmailService;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmailServiceTest {

  @Autowired
  private EmailService emailService;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    // 테스트 시작 전에 쿨타임 키 삭제 (초기화)
    redisTemplate.delete("email:request:hschung135@naver.com");
  }

  @Test
  void 이메일_인증코드_발송_성공() {
    // given
    String email = "hschung135@naver.com";

    // when
    emailService.sendVerificationCode(email);

    // then
    String code = redisTemplate.opsForValue().get("email:verify:" + email);
    String cooltime = redisTemplate.opsForValue().get("email:request:" + email);

    assertThat(code).isNotNull();
    assertThat(cooltime).isEqualTo("cooltime");
  }

  @Test
  void 이메일_인증_성공처리() {
    // given
    String email = "hschung135@naver.com";
    emailService.sendVerificationCode(email);
    String code = redisTemplate.opsForValue().get("email:verify:" + email);

    // when
    boolean result = emailService.verifyCode(email, code);

    // then
    assertThat(result).isTrue();
    String verified = redisTemplate.opsForValue().get("email:verified:" + email);
    assertThat(verified).isEqualTo("true");
  }

  @Test
  void 이메일_인증_실패처리() {
    // given
    String email = "hschung135@naver.com";
    emailService.sendVerificationCode(email);

    // when
    boolean result = emailService.verifyCode(email, "wrongcode");

    // then
    assertThat(result).isFalse();
  }

  @Test
  void 회원가입_완료후_Redis_키_삭제() {
    // given
    String email = "hschung135@naver.com";
    emailService.sendVerificationCode(email);
    emailService.markAsVerified(email);

    // when
    emailService.clearVerificationData(email);

    // then
    assertThat(redisTemplate.hasKey("email:verify:" + email)).isFalse();
    assertThat(redisTemplate.hasKey("email:verified:" + email)).isFalse();
  }

  @Test
  void 인증코드_TTL_만료후_삭제() throws InterruptedException {
    // given
    String email = "hschung135@naver.com";
    emailService.sendVerificationCode(email);

    // when - Redis TTL을 강제로 1초로 변경하고 기다리기
    redisTemplate.expire("email:verify:" + email, Duration.ofSeconds(1));
    Thread.sleep(1500); // 1.5초 대기

    // then
    String code = redisTemplate.opsForValue().get("email:verify:" + email);
    assertThat(code).isNull();
  }


  @Test
  void 쿨타임_중복_요청_차단() {
    // given
    String email = "hschung135@naver.com";
    emailService.sendVerificationCode(email);

    // when + then
    assertThatThrownBy(() -> emailService.sendVerificationCode(email))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("너무 자주 요청했습니다");
  }

  @Test
  void 저장된_코드_없이_인증_실패() {
    // given
    String email = "hschung135@naver.com";

    // when
    boolean result = emailService.verifyCode(email, "anycode");

    // then
    assertThat(result).isFalse();
  }

}

