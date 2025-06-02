package com.ssafy.musoonzup.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import com.ssafy.musoonzup.member.dao.MemberDao;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import com.ssafy.musoonzup.member.dto.MemberDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional  // 테스트 끝나면 롤백하기 위함
public class MemberDaoTest {

  @Autowired
  private MemberDao memberDao;

  @Autowired
  private MemberAccountDao memberAccountDao;

  @Test
  void 회원가입_테스트() {
    // given
    MemberDto member = MemberDto.builder()
        .name("정한슬")
        .email("hanseul@ssafy.com")
        .phone("010-1234-5678")
        .gender(1)
        .birthDate(LocalDate.of(1998, 3, 15))
        .build();

    // when
    int insertCount = memberDao.insert(member);

    // then
    assertEquals(1, insertCount);
    assertNotNull(member.getIdx());
    assertNotNull(member.getCreatedAt());
    assertNotNull(member.getUpdatedAt());

    MemberAccountDto account = MemberAccountDto.builder()
        .memberIdx(member.getIdx())
        .id("hanseul99")
        .pw("encoded-password")
        .role(1L)
        .build();

    int accountInsertCount = memberAccountDao.insert(account);
    assertEquals(1, accountInsertCount);
  }
}