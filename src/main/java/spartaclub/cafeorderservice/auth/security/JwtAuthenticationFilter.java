package spartaclub.cafeorderservice.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 인증 필터
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;           // JWT 검증/파싱 담당
    private final CustomUserDetailsService userDetailsService; // uuid → User 조회 담당

    //실제 필터 로직: 요청이 들어올 때마다 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = extractToken(request);

        // 2. 토큰이 있고, 유효한 경우에만 인증 처리
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰에서 uuid 꺼내기
            String uuid = jwtTokenProvider.getUuidFromToken(token);

            // 4. uuid로 DB에서 사용자 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(uuid);

            // 5. Spring Security 인증 객체 생성
            //    (아이디, 비밀번호, 권한) 형태로 감싸는 객체
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,    // principal: 나중에 @AuthenticationPrincipal로 꺼낼 객체
                            null,           // credentials: 비밀번호 (필터에서는 null로 설정)
                            userDetails.getAuthorities() // 권한 목록
                    );

            // 6. 요청 상세 정보(IP 등) 추가
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 7. SecurityContextHolder에 인증 정보 저장
            //    이 시점부터 "이 요청은 인증된 사용자의 요청" 으로 인식됨
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("[JWT Filter] 인증 성공: uuid={}", uuid);
        }

        // 8. 다음 필터로 요청 전달 (필터 체인 계속 진행)
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 Bearer 토큰 추출
    private String extractToken(HttpServletRequest request) {
        // Authorization 헤더 값 읽기
        String bearerToken = request.getHeader("Authorization");

        // "Bearer "로 시작하는 경우에만 토큰 추출 (7글자 이후부터)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null; // 토큰 없음
    }
}
