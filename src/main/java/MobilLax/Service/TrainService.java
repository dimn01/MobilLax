/**
 * TrainService.java
 *
 * ✅ 파일 목적: 전철(KTX, ITX 등) 정보 조회를 위한 공공데이터포털 API 연동 서비스
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-11
 */

package MobilLax.Service;

import MobilLax.Config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * ✅ 클래스 설명:
 * - 전철(KTX, ITX 등) 관련 공공API를 호출하여 출도착 정보 및 역 목록, 열차 종류, 도시코드를 조회합니다.
 */
@Service
@RequiredArgsConstructor
public class TrainService {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    /** ✅ 공통 요청 메서드 */
    private Mono<String> sendRequest(URI uri) {
        return webClient.get()
                .uri(uri)
                .header("Accept", "application/xml")
                .retrieve()
                .bodyToMono(String.class);
    }

    // ----------------------------
    // 🚄 전철 API
    // ----------------------------

    /** 출/도착지 기반 열차 정보 조회 */
    public Mono<String> getTrainInfo(String depPlaceId, String arrPlaceId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&depPlaceId=%s&arrPlaceId=%s",
                apiProperties.getTrain().getInfo(),
                apiProperties.getKey(), depPlaceId, arrPlaceId));
        return sendRequest(uri);
    }

    /** 시/도별 기차역 목록 조회 */
    public Mono<String> getStationList(String cityCode) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s",
                apiProperties.getTrain().getStationList(),
                apiProperties.getKey(), cityCode));
        return sendRequest(uri);
    }

    /** 열차 종류 목록 조회 */
    public Mono<String> getTrainTypes() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getTrain().getType(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    /** 도시코드 목록 조회 */
    public Mono<String> getCtyCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getTrain().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }
}