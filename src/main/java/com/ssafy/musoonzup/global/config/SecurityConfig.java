package com.ssafy.musoonzup.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.ssafy.musoonzup.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
  final private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // 우선 전체 api 허용
        .authorizeHttpRequests(auth -> auth
            // 로그인, 인증은 모두 허용
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/members/register",
                "/members/send-verification",
                "/members/verify-code",
                "/members/login",
                "/members/check-id",
                "/auth/refresh-token",
                "/members/find-password",
                "/applyhome/**",
                "/community/**",
                "/notice/**",
                "/noticeComment/**"
            // ,"/openai/**" // GPT 연결 테스트 경로
            // ,"/route/**" // 네비 테스트 경로
            ).permitAll()
            // MASTER만 가능
            .requestMatchers("/master/role").hasRole("MASTER")

            // ADMIN 이상만 가능
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/community/admin/**").hasRole("ADMIN")
            .requestMatchers("/notice/admin/**").hasRole("ADMIN")
            .requestMatchers("/noticeComment/admin/**").hasRole("ADMIN")

            // MEEBERSHIP 이상만 가능
            .requestMatchers("applyhome/ms/**").hasRole("MEMBERSHIP")

            // USER 이상
            .requestMatchers("/members/**").hasRole("USER")
            .requestMatchers("/community/member/**").hasRole("USER")
            .requestMatchers("/applylike/**").hasRole("USER")
            .requestMatchers("/noticeComment/member/**").hasRole("USER")
            .requestMatchers("/route/**").hasRole("USER")

            // 나머지 요청은 허용 - 잘못된 경로(없는 경로)는 404로 띄우기 위함
            .anyRequest().permitAll())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // 기본 폼 로그인 비활성화
        .formLogin(form -> form.disable())
        // HTTP Basic 인증도 비활성화
        .httpBasic(basic -> basic.disable());

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept"));
    config.setAllowCredentials(true); // 이걸 꼭 켜야 쿠키 전송 허용됨

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("MASTER").implies("ADMIN")
        .role("ADMIN").implies("MEMBERSHIP")
        .role("MEMBERSHIP").implies("USER")
        .build();
  }
}
