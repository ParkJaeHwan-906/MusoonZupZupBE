package com.ssafy.musoonzup.admin.service;

import com.ssafy.musoonzup.admin.dto.MemberWithAccountDto;
import com.ssafy.musoonzup.applyHome.dto.SearchCondition;
import com.ssafy.musoonzup.global.searchCondition.SearchCondition_TMP;
import com.ssafy.musoonzup.global.security.CustomUserDetails;
import com.ssafy.musoonzup.member.dao.MemberAccountDao;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final MemberAccountDao memberAccountDao;
  private static final Set<String> ALLOWED_SEARCH_KEYS = Set.of("email", "name", "id", "role");
  private static final Set<String> ALLOWED_SORT_KEYS = Set.of("createdAt","name");

  private String resolveSearchKey(String key) {
    return ALLOWED_SEARCH_KEYS.contains(key) ? key : null;
  }

  private String resolveSortKey(String sortKey) {
    return ALLOWED_SORT_KEYS.contains(sortKey) ? sortKey : "createdAt";
  }

  private String resolveSortValue(String sortValue) {
    return "ASC".equalsIgnoreCase(sortValue) ? "ASC" : "DESC";
  }

  @Transactional(readOnly = true)
  public Page<MemberWithAccountDto> getAllMembers(CustomUserDetails loginUser, SearchCondition_TMP condition) {
    if (!Set.of("MASTER", "ADMIN").contains(loginUser.getLoginMemberDto().getRole())) {
      throw new RuntimeException("접근 권한이 없습니다.");
    }

    condition.setKey(resolveSearchKey(condition.getKey()));
    condition.setSortKey(resolveSortKey(condition.getSortKey()));
    condition.setSortValue(resolveSortValue(condition.getSortValue()));
    // TODO : xml에서 bind 할지? 아니면 재 검증해놓을지?
    condition.setOffset(condition.getPage() * condition.getSize());
    List<MemberWithAccountDto> results = memberAccountDao.findAllWithAccount(condition);
    int total = memberAccountDao.countAllWithAccount(condition);

    Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize());
    return new PageImpl<>(results, pageable, total);
  }

  @Transactional
  public boolean banMember(Long memberIdx) {
    int updateCount = memberAccountDao.updateBanStatus(memberIdx, 1);
    return updateCount == 1;
  }

  @Transactional
  public boolean unbanMember(Long memberIdx) {
    int updateCount = memberAccountDao.updateBanStatus(memberIdx, 0);
    return updateCount == 1;
  }
}
