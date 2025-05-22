/**
 * CityBusService.java
 *
 * ✅ 파일 목적: 공공데이터포털의 시내버스 관련 API들을 호출하여
 *              XML 데이터를 비동기로 받아오는 기능을 제공하는 서비스 클래스입니다.
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
 * 시내버스 관련 OpenAPI를 호출하여 도착정보, 노선정보, 위치정보, 정류소정보 등을 조회하는 서비스입니다.
 * WebClient를 이용한 비동기 HTTP 요청을 통해 XML 응답을 수신하며,
 * ApiProperties 설정 클래스에서 API 키와 URL을 동적으로 불러옵니다.
 */
@Service
@RequiredArgsConstructor
public class CityBusService {

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
    // 🚏 도착 정보
    // ----------------------------

    public Mono<String> getArrivalInfo(String cityCode, String nodeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&nodeId=%s",
                apiProperties.getCitybus().getArrivalAll(),
                apiProperties.getKey(), cityCode, nodeId));
        return sendRequest(uri);
    }

    public Mono<String> getArrivalInfoByRoute(String cityCode, String nodeId, String routeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&nodeId=%s&routeId=%s",
                apiProperties.getCitybus().getArrivalSpecific(),
                apiProperties.getKey(), cityCode, nodeId, routeId));
        return sendRequest(uri);
    }

    public Mono<String> getArrivalCityCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getCitybus().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    // ----------------------------
    // 🗺️ 노선 정보
    // ----------------------------

    public Mono<String> getRouteInfo(String cityCode, String routeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&routeId=%s",
                apiProperties.getCitybus().getRouteInfo(),
                apiProperties.getKey(), cityCode, routeId));
        return sendRequest(uri);
    }

    public Mono<String> getRouteNumberList(String cityCode, String routeNo) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&routeNo=%s",
                apiProperties.getCitybus().getRouteNumber(),
                apiProperties.getKey(), cityCode, URLEncoder.encode(routeNo, StandardCharsets.UTF_8)));
        return sendRequest(uri);
    }

    public Mono<String> getRouteStopList(String cityCode, String routeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&routeId=%s",
                apiProperties.getCitybus().getRouteStops(),
                apiProperties.getKey(), cityCode, routeId));
        return sendRequest(uri);
    }

    public Mono<String> getRouteCityCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getCitybus().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    // ----------------------------
    // 🚌 위치 정보
    // ----------------------------

    public Mono<String> getBusLocationList(String cityCode, String routeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&routeId=%s",
                apiProperties.getCitybus().getLocation(),
                apiProperties.getKey(), cityCode, routeId));
        return sendRequest(uri);
    }

    public Mono<String> getBusLocationNearStation(String cityCode, String nodeId, String routeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&nodeId=%s&routeId=%s",
                apiProperties.getCitybus().getSttnLocation(),
                apiProperties.getKey(), cityCode, nodeId, routeId));
        return sendRequest(uri);
    }

    public Mono<String> getBusLocationCityCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getCitybus().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }

    // ----------------------------
    // 🏙️ 정류소 정보
    // ----------------------------

    public Mono<String> getNearbyStations(double gpsLati, double gpsLong) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&gpsLati=%s&gpsLong=%s",
                apiProperties.getCitybus().getStationNearby(),
                apiProperties.getKey(), gpsLati, gpsLong));
        return sendRequest(uri);
    }

    public Mono<String> getStationNoList(String cityCode, String nodeNm) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&nodeNm=%s",
                apiProperties.getCitybus().getStationNo(),
                apiProperties.getKey(), cityCode, URLEncoder.encode(nodeNm, StandardCharsets.UTF_8)));
        return sendRequest(uri);
    }

    public Mono<String> getStationRouteList(String cityCode, String nodeId) {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml&cityCode=%s&nodeId=%s",
                apiProperties.getCitybus().getStationRoute(),
                apiProperties.getKey(), cityCode, nodeId));
        return sendRequest(uri);
    }

    public Mono<String> getStationCityCodeList() {
        URI uri = URI.create(String.format("%s?serviceKey=%s&_type=xml",
                apiProperties.getCitybus().getCtycode(),
                apiProperties.getKey()));
        return sendRequest(uri);
    }
}