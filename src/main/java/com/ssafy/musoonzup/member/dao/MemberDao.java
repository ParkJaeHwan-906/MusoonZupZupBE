package com.ssafy.musoonzup.member.dao;

import com.ssafy.musoonzup.member.dto.MemberDto;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDao {
  int insert(MemberDto member);
  int updatePW(int idx, String pw);
  Optional<MemberDto> findByIdx(long idx);
}
