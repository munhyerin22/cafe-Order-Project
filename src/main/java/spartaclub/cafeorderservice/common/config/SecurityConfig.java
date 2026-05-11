package spartaclub.cafeorderservice.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spartaclub.cafeorderservice.auth.security.JwtAuthenticationFilter;

/**
 * Spring Security 전체 설정 클래스
 * - JWT 기반 Stateless 인증 구조
 * - 세션 사용 안 함 (JWT로 대체)
 * - CSRF 비활성화 (REST API이므로 불필요)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 우리가 만든 JWT 필터 주입
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화: REST API는 세션이 없으므로 불필요
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 정책: STATELESS → 서버가 세션을 만들지 않음 (JWT로 대체)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청별 인증 설정
                .authorizeHttpRequests(auth ->
                        auth
                                // 인증 없이 접근 가능한 경로 (회원가입, 로그인, 메뉴 조회)
                                .requestMatchers(
                                        "/api/v1/users/signup",
                                        "/api/v1/users/login",
                                        "/api/v1/menus"
                                ).permitAll()
                                // 그 외 모든 요청은 JWT 인증 필요
                                .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
                // 요청이 들어오면 JWT 필터가 먼저 실행되어 인증 처리
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt 비밀번호 암호화 Bean (회원가입/로그인 시 PIN 암호화에 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
