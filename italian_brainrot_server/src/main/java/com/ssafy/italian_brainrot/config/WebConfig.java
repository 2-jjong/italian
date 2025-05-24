package com.ssafy.italian_brainrot.config;

import com.ssafy.italian_brainrot.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 허용
                .allowedOriginPatterns("*")  // 모든 Origin 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true)  // 쿠키 포함 요청 허용
                .maxAge(3600);  // preflight 요청 캐시 시간 (1시간)
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 인증이 필요한 URL 패턴
                .addPathPatterns(
                        "/user/token",           // FCM 토큰 업데이트
                        "/user/point",           // 포인트 충전
                        "/user",                 // 사용자 정보 조회 (GET)
                        "/comment/**",           // 댓글 관련 모든 API
                        "/order/**",             // 주문 관련 모든 API
                        "/inventory/**",         // 인벤토리 관련 모든 API
                        "/card/**",              // 카드 관련 모든 API
                        "/recipe/**",            // 레시피 관련 모든 API
                        "/battle/**"             // 배틀 관련 모든 API
                )
                // 인증이 불필요한 URL 패턴 (제외)
                .excludePathPatterns(
                        "/user",                 // 회원가입 (POST)
                        "/user/login",           // 로그인
                        "/user/logout",          // 로그아웃
                        "/user/isUsed/**",       // 아이디 중복 확인
                        "/product/**",           // 상품 조회 (인증 불필요)
                        "/swagger-ui/**",        // Swagger UI
                        "/v3/api-docs/**",       // API 문서
                        "/api-docs/**"           // OpenAPI JSON 문서
                );
    }
}