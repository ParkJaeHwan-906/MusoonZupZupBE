package com.ssafy.musoonzup.global.email.service;

import com.ssafy.musoonzup.global.redis.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final RedisService redisService;

  public String sendVerificationCode(String email) {
    String requestKey = "email:request:" + email;
    String verifyKey = "email:verify:" + email;

    if (redisService.hasKey(requestKey)) {
      throw new IllegalStateException("너무 자주 요청했습니다. 잠시 후 다시 시도해주세요.");
    }

    String code = generateCode();
    redisService.set(verifyKey, code, Duration.ofMinutes(5));
    redisService.set(requestKey, "cooltime", Duration.ofMinutes(2));

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("[무순줍줍] 이메일 인증 코드입니다.");
    message.setText("인증 코드: " + code);
    mailSender.send(message);

    return code;
  }

  public boolean verifyCode(String email, String inputCode) {
    String savedCode = redisService.get("email:verify:" + email);
    if (inputCode.equals(savedCode)) {
      redisService.set("email:verified:" + email, "true", Duration.ofMinutes(30));
      return true;
    }
    return false;
  }

  private String generateCode() {
    return String.valueOf((int) (Math.random() * 900000 + 100000));
  }

  public void markAsVerified(String email) {
    redisService.set("email:verified:" + email, "true", Duration.ofMinutes(30));
  }

  public boolean isEmailVerified(String email) {
    return "true".equals(redisService.get("email:verified:" + email));
  }

  public void clearVerificationData(String email) {
    redisService.delete("email:verify:" + email);
    redisService.delete("email:verified:" + email);
  }

  public void sendTempPassword(String to, String tempPassword) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("[무순줍줍] 임시 비밀번호 발급 안내");
    message.setText("임시 비밀번호: " + tempPassword + "\n로그인 후 비밀번호를 반드시 변경해 주세요.");
    mailSender.send(message);
  }
}

