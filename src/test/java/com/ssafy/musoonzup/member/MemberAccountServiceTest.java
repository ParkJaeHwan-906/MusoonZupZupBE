package com.ssafy.musoonzup.member;

import com.ssafy.musoonzup.member.service.MemberAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberAccountServiceTest {

  @Autowired
  private MemberAccountService memberAccountService;

  @Test
  void 아이디가_중복되면_true를_반환한다() {
    // given
    String duplicatedId = "hansul99";

    // TODO: 이 아이디는 DB에 미리 삽입돼 있어야 테스트가 성공해 (또는 직접 삽입)

    // when
    boolean result = memberAccountService.isIdDuplicated(duplicatedId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void 아이디가_중복되지_않으면_false를_반환한다() {
    // given
    String availableId = "newuniqueid123";

    // when
    boolean result = memberAccountService.isIdDuplicated(availableId);

    // then
    assertThat(result).isFalse();
  }
}
