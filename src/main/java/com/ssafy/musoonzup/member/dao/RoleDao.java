package com.ssafy.musoonzup.member.dao;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao {
  String findNameByIdx(Long idx);
  Optional<Long> findIdxByName(String roleName);
}
