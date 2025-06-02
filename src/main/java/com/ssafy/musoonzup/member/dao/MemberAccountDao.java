package com.ssafy.musoonzup.member.dao;

import com.ssafy.musoonzup.admin.dto.MemberWithAccountDto;
import com.ssafy.musoonzup.applyHome.dto.SearchCondition;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.member.dto.MemberAccountDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberAccountDao {
  int insert(MemberAccountDto memberAccount);
  int countById(@Param("id") String id);
  Optional<MemberAccountDto> findById(@Param("id") String id);
  Optional<MemberAccountDto> findByIdNameEmail(@Param("id") String id,
      @Param("name") String name,
      @Param("email") String email);
  int updatePasswordById(@Param("id") String id,
      @Param("pw") String pw);
  Optional<MemberAccountDto> findByIdx(@Param("idx") Long memberAccountIdx);
  String findIdByMemberIdx(@Param("memberIdx") Long memberIdx);
  Optional<MemberAccountDto> findByMemberIdx(@Param("memberIdx") Long memberIdx);

  int updateRole(MemberAccountDto memberAccountDto);

  List<MemberWithAccountDto> findAllWithAccount(@Param("sc") SearchCondition_TMP searchCondition);
  int countAllWithAccount(@Param("sc") SearchCondition_TMP searchCondition);
  int updateBanStatus(@Param("memberIdx") Long memberIdx, @Param("isBanned") int isBanned);
  
  // MemberShip Upgrade
  int membershipUpgrage(@Param("memberIdx") Long memberIdx);
}
