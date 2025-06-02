package com.ssafy.musoonzup.global.jwt;

import com.ssafy.musoonzup.global.security.CustomUserDetailsService;
import com.ssafy.musoonzup.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    log.info("doFilterInternal 진입");
    if (header != null) { // 우선 Bearer 로 구분 안하고 null값 아니면 split 후 token 분석하는 것으로 구현
      try {
        String token = header.split(" ")[1];
        Map<String, Object> payload = jwtUtil.parseEncryptedPayload(token);
        String username = (String) payload.get("id");

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
      } catch (Exception e) {
        // 토큰 오류 → SecurityContext 비우기
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}

