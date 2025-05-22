/*
 * KakaoMapService.java
 * ✅ 목적: 카카오 로컬 API를 통해 지명(키워드)을 좌표(위도/경도)로 변환하는 서비스
 */
package MobilLax.Domain.Transit.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private static final Logger log = LoggerFactory.getLogger(KakaoMapService.class);

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ✅ 지명 키워드로 좌표(위도, 경도)를 조회
     * @param keyword 지명 또는 주소 (예: 서울역, 부산역 등)
     * @return [위도, 경도] 배열 (lat, lon) 또는 null
     */
    public double[] geocode(String keyword) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                    .queryParam("query", keyword)
                    .build()
                    .toUriString();

            String response = WebClient.create()
                    .get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode documents = objectMapper.readTree(response).path("documents");

            if (documents.isArray() && !documents.isEmpty()) {
                JsonNode first = documents.get(0);
                double lat = first.path("y").asDouble();
                double lon = first.path("x").asDouble();

                // ✅ 0번째 좌표만 로그에 출력
                log.info("📍 [KakaoMap 좌표 변환] \"{}\" → 위도: {}, 경도: {}", keyword, lat, lon);

                return new double[]{lat, lon};
            } else {
                log.warn("⚠️ [KakaoMap 좌표 변환 실패] 키워드 \"{}\"에 대한 결과 없음", keyword);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Kakao 좌표 변환 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
