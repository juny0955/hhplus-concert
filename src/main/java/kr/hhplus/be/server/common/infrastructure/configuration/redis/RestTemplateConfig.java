package kr.hhplus.be.server.common.infrastructure.configuration.redis;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정
 * 외부 API 호출을 위한 HTTP 클라이언트 구성
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 외부 API 호출용 RestTemplate 빈 생성
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(10))      // 읽기 타임아웃: 10초
            .build();
    }
}