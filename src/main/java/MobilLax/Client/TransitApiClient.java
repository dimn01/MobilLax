/*
 * TransitApiClient.java
 * ✅ 목적: Tmap 대중교통 API 요청 전용 유틸 클래스
 */
package MobilLax.Client;

import MobilLax.Domain.Transit.Dto.TransitRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TransitApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${tmap.api.key}")
    private String tmapApiKey;

    // ✅ 요약 경로 요청
    public String requestSummaryRoute(TransitRequestDto requestDto) throws Exception {
        String url = "https://apis.openapi.sk.com/transit/routes/sub";
        return requestToTmapApi(url, requestDto);
    }

    // ✅ 상세 경로 요청
    public String requestDetailRoute(TransitRequestDto requestDto) throws Exception {
        String url = "https://apis.openapi.sk.com/transit/routes/"; // 예시 URL
        return requestToTmapApi(url, requestDto);
    }

    // ✅ 공통 POST 요청 처리 메서드
    private String requestToTmapApi(String url, TransitRequestDto requestDto) throws Exception {
        String payload = objectMapper.writeValueAsString(requestDto);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("appKey", tmapApiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
