/**
 * WebClientConfig.java
 *
 * ✅ 파일 목적: WebClient 인스턴스를 전역에서 주입받을 수 있도록 Bean으로 등록하는 Spring 설정 클래스입니다.
 *              외부 Open API 요청 등을 비동기 방식으로 처리할 때 사용됩니다.
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-11
 */

package MobilLax.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ✅ 클래스 설명:
 * WebClient는 비동기 방식의 HTTP 요청을 지원하는 Spring WebFlux의 클라이언트입니다.
 * 이 클래스는 WebClient를 Spring Bean으로 등록하여 의존성 주입을 통해 다양한 서비스에서 재사용할 수 있도록 설정합니다.
 */
@Configuration
public class WebClientConfig {

    /**
     * ✅ WebClient Bean 등록 (버퍼 크기 확장 포함)
     *
     * 외부 API에서 대용량 응답(JSON/XML 등)을 처리하기 위해
     * 기본 256KB → 10MB로 메모리 버퍼 사이즈를 확장합니다.
     *
     * @return WebClient 인스턴스
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024)) // 10MB로 확장
                        .build())
                .build();
    }
}
