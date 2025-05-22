/**
 * ExpressBusService.java
 *
 * ✅ 파일 목적: 고속버스 도착정보 및 운행정보 조회를 위한 공공데이터포털 API 연동 서비스
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-11
 */

package MobilLax.Domain.Terminal.Service;

import MobilLax.Config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ✅ 클래스 설명:
 * - 고속버스 도착정보 및 스케줄 API를 비동기로 호출하여 XML 데이터를 반환합니다.
 * - 도착정보와 운행정보, 터미널 및 등급, 도시코드 조회 기능 포함
 */
@Service
@RequiredArgsConstructor
public class ExpressBusService {

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
    // 🚍 도착정보 API
    // ----------------------------

    public Mono<String> getArrivalTerminalList(String terminalName) {
        String encoded = URLEncoder.encode(terminalName, StandardCharsets.UTF_8);
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&terminalNm=%s",
                apiProperties.getExpress().getArrivalTmn(),
                apiProperties.getKey(), encoded));
        return sendRequest(uri);
    }

    public Mono<String> getArrivalInfo(String depTmnId, String arrTmnId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&depTmnCd=%s&arrTmnCd=%s",
                apiProperties.getExpress().getArrivalInfo(),
                apiProperties.getKey(), depTmnId, arrTmnId));
        return sendRequest(uri);
    }

    // ----------------------------
    // 🚌 운행 정보 API
    // ----------------------------

    public Mono<String> getSchedule(String depTmnId, String arrTmnId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&depTmnCd=%s&arrTmnCd=%s",
                apiProperties.getExpress().getSchedule(),
                apiProperties.getKey(), depTmnId, arrTmnId));
        return sendRequest(uri);
    }

    public Mono<String> getTerminalList(String terminalName) {
        String encoded = URLEncoder.encode(terminalName, StandardCharsets.UTF_8);
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&terminalNm=%s",
                apiProperties.getExpress().getTerminal(),
                apiProperties.getKey(), encoded));
        return sendRequest(uri);
    }

    public Mono<String> getGradeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getExpress().getGrade(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    public Mono<String> getCtyCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getExpress().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }
}