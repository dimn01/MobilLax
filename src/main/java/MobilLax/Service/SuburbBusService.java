/**
 * SuburbBusService.java
 *
 * ✅ 파일 목적: 공개데이터포털 TAGO 시외버스 관련 OpenAPI 호출 기능을 제공하는 서비스 클래스입니다.
 *               (터미널 목록, 시간표, 등급, 도시코드 조회)
 *
 * 작업자: 김영빈
 * 마지막 수정일: 2025-05-11
 */

package MobilLax.Service;

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
 * - 시외버스 OpenAPI 요청을 WebClient로 수행하여 XML 데이터를 비동기로 받아온다.
 * - ApiProperties 에 설정된 URL 값과 인증 키를 활용한 정사키 요청 수행.
 */
@Service
@RequiredArgsConstructor
public class SuburbBusService {

    private final WebClient webClient;
    private final ApiProperties apiProperties;

    /**
     * ✅ 터미널 목록 조회 API
     * - terminalNm (시외버스 터미널 이름) 사용
     */
    public Mono<String> getTerminalList(String terminalName) {
        String encoded = URLEncoder.encode(terminalName, StandardCharsets.UTF_8);
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&terminalNm=%s",
                apiProperties.getSuburb().getTerminal(),
                apiProperties.getKey(), encoded));
        return sendRequest(uri);
    }

    /**
     * ✅ 출발/도착지 기반 시외버스 시간표 조회
     * - depTerminalId, arrTerminalId, depPlandTime 입력 필요
     */
    public Mono<String> getSchedule(String depTerminalId, String arrTerminalId, String depPlandTime) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&depTerminalId=%s&arrTerminalId=%s&depPlandTime=%s",
                apiProperties.getSuburb().getSchedule(),
                apiProperties.getKey(),
                depTerminalId, arrTerminalId, depPlandTime));
        return sendRequest(uri);
    }

    /**
     * ✅ 시외버스 등급 목록 조회
     */
    public Mono<String> getGradeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getSuburb().getGrade(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    /**
     * ✅ 시외버스 가능 지역의 도시코드 목록 조회
     */
    public Mono<String> getCtyCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getSuburb().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    /**
     * ✅ WebClient 공통 요청 수행 메서드
     */
    private Mono<String> sendRequest(URI uri) {
        return webClient.get()
                .uri(uri)
                .header("Accept", "application/xml")
                .retrieve()
                .bodyToMono(String.class);
    }
}