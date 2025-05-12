/**
 * TransitService.java
 * ✅ 파일 목적: SK Tmap API를 활용한 대중교통 경로 요약 및 상세 조회 서비스 로직 구현
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-12
 */

package MobilLax.Service;

import MobilLax.Model.Dto.TransitDetailDto;
import MobilLax.Model.Dto.TransitRequestDto;
import MobilLax.Model.Dto.TransitSummaryDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ 클래스 설명:
 * Tmap 대중교통 API를 활용하여 최적 경로 요약 및 상세 정보를 조회하는 서비스 로직
 * - 최적 요약 정보 조회 (요금, 시간, 환승 수 등)
 * - 상세 이동 경로 조회 (정류장, 구간 정보 등)
 */
@Service
@RequiredArgsConstructor
public class TransitService {

    private static final Logger log = LoggerFactory.getLogger(TransitService.class);

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${tmap.api.key}")
    private String tmapApiKey;

    /**
     * ✅ TransitRequestDto 기반 상세 경로 요청
     * @param request 출발지와 도착지 좌표를 담은 요청 객체
     * @return 상세 경로 정보 DTO
     */
    public TransitDetailDto getRouteDetail(TransitRequestDto request) {
        return getRouteDetail(
                Double.parseDouble(request.getStartX()),
                Double.parseDouble(request.getStartY()),
                Double.parseDouble(request.getEndX()),
                Double.parseDouble(request.getEndY())
        );
    }

    /**
     * ✅ TransitRequestDto 기반 요약 경로 요청
     * @param request 출발지와 도착지 좌표를 담은 요청 객체
     * @return 요약 경로 정보 DTO
     */
    public TransitSummaryDto getBestRouteSummary(TransitRequestDto request) {
        return getBestRouteSummary(
                Double.parseDouble(request.getStartX()),
                Double.parseDouble(request.getStartY()),
                Double.parseDouble(request.getEndX()),
                Double.parseDouble(request.getEndY())
        );
    }

    /**
     * ✅ Tmap API를 호출하여 상세 경로 조회
     * @param startX 출발지 경도
     * @param startY 출발지 위도
     * @param endX 도착지 경도
     * @param endY 도착지 위도
     * @return TransitDetailDto 상세 경로 정보
     */
    public TransitDetailDto getRouteDetail(double startX, double startY, double endX, double endY) {
        String url = "https://apis.openapi.sk.com/transit/routes";

        try {
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("startX", String.valueOf(startX));
            payloadMap.put("startY", String.valueOf(startY));
            payloadMap.put("endX", String.valueOf(endX));
            payloadMap.put("endY", String.valueOf(endY));
            payloadMap.put("format", "json");

            String payload = objectMapper.writeValueAsString(payloadMap);

            Mono<String> responseMono = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header("appKey", tmapApiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();
            log.info("===== TMAP 상세 응답 JSON =====\n{}\n================================", response);

            JsonNode root = objectMapper.readTree(response);
            JsonNode itineraries = root.path("metaData").path("plan").path("itineraries");

            if (itineraries.isMissingNode() || !itineraries.isArray() || itineraries.isEmpty()) {
                throw new RuntimeException("상세 경로가 존재하지 않습니다.");
            }

            JsonNode first = itineraries.get(0);
            TransitDetailDto detailDto = new TransitDetailDto();
            detailDto.setTotalTime(first.path("totalTime").asInt());
            detailDto.setTotalFare(first.path("fare").path("regular").path("totalFare").asInt());
            detailDto.setTransferCount(first.path("transferCount").asInt());

            List<TransitDetailDto.TransitLeg> legs = new ArrayList<>();
            JsonNode legArray = first.path("legs");
            if (legArray.isArray()) {
                for (JsonNode leg : legArray) {
                    TransitDetailDto.TransitLeg item = new TransitDetailDto.TransitLeg();
                    item.setMode(leg.path("mode").asText(null));
                    item.setSectionTime(leg.path("sectionTime").asInt());
                    item.setDistance(leg.path("distance").asInt());
                    item.setRoute(leg.path("route").asText(null));
                    item.setRouteId(leg.path("routeId").asText(null));
                    item.setRouteColor(leg.path("routeColor").asText(null));
                    item.setType(leg.path("type").asInt());
                    item.setService(leg.path("service").asInt());
                    item.setPassShapeLineString(leg.path("passShapeLineString").asText(null));

                    JsonNode start = leg.path("start");
                    TransitDetailDto.StopInfo startInfo = new TransitDetailDto.StopInfo();
                    startInfo.setName(start.path("name").asText(null));
                    startInfo.setLat(start.path("lat").asDouble());
                    startInfo.setLon(start.path("lon").asDouble());
                    item.setStart(startInfo);

                    JsonNode end = leg.path("end");
                    TransitDetailDto.StopInfo endInfo = new TransitDetailDto.StopInfo();
                    endInfo.setName(end.path("name").asText(null));
                    endInfo.setLat(end.path("lat").asDouble());
                    endInfo.setLon(end.path("lon").asDouble());
                    item.setEnd(endInfo);

                    List<TransitDetailDto.Step> steps = new ArrayList<>();
                    JsonNode stepArray = leg.path("steps");
                    if (stepArray.isArray()) {
                        for (JsonNode step : stepArray) {
                            TransitDetailDto.Step s = new TransitDetailDto.Step();
                            s.setStreetName(step.path("streetName").asText(null));
                            s.setDistance(step.path("distance").asDouble());
                            s.setDescription(step.path("description").asText(null));
                            s.setLinestring(step.path("linestring").asText(null));
                            steps.add(s);
                        }
                    }
                    item.setSteps(steps);

                    List<TransitDetailDto.Station> stations = new ArrayList<>();
                    JsonNode stationArray = leg.path("passStopList").path("stations");
                    if (stationArray.isArray()) {
                        for (JsonNode st : stationArray) {
                            TransitDetailDto.Station station = new TransitDetailDto.Station();
                            station.setStationID(st.path("stationID").asText(null));
                            station.setStationName(st.path("stationName").asText(null));
                            station.setLat(st.path("lat").asDouble());
                            station.setLon(st.path("lon").asDouble());
                            stations.add(station);
                        }
                    }
                    item.setStations(stations);
                    legs.add(item);
                }
            }
            detailDto.setLegs(legs);
            return detailDto;

        } catch (Exception e) {
            log.error("상세 경로 파싱 실패", e);
            throw new RuntimeException("상세 경로 파싱 실패", e);
        }
    }

    /**
     * ✅ Tmap API를 호출하여 요약 경로 조회
     * @param startX 출발지 경도
     * @param startY 출발지 위도
     * @param endX 도착지 경도
     * @param endY 도착지 위도
     * @return TransitSummaryDto 요약 경로 정보
     */
    public TransitSummaryDto getBestRouteSummary(double startX, double startY, double endX, double endY) {
        String url = "https://apis.openapi.sk.com/transit/routes";

        try {
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("startX", String.valueOf(startX));
            payloadMap.put("startY", String.valueOf(startY));
            payloadMap.put("endX", String.valueOf(endX));
            payloadMap.put("endY", String.valueOf(endY));
            payloadMap.put("format", "json");

            String payload = objectMapper.writeValueAsString(payloadMap);

            Mono<String> responseMono = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header("appKey", tmapApiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class);

            String response = responseMono.block();

            if (response != null) {
                int sizeInBytes = response.getBytes(StandardCharsets.UTF_8).length;
                log.info("✅ 응답 크기: {} bytes (약 {} KB)", sizeInBytes, sizeInBytes / 1024);
            }

            log.info("===== TMAP 요약 응답 JSON =====\n{}\n================================", response);

            JsonNode root = objectMapper.readTree(response);
            JsonNode itineraries = root.path("metaData").path("plan").path("itineraries");

            if (itineraries.isMissingNode() || !itineraries.isArray() || itineraries.isEmpty()) {
                throw new RuntimeException("요약 경로가 존재하지 않습니다.");
            }

            JsonNode first = itineraries.get(0);
            TransitSummaryDto summary = new TransitSummaryDto();
            summary.setTotalTime(first.path("totalTime").asInt());
            summary.setTotalFare(first.path("fare").path("regular").path("totalFare").asInt());
            summary.setTransferCount(first.path("transferCount").asInt());
            summary.setTotalDistance(first.path("totalDistance").asInt());
            summary.setTotalWalkTime(first.path("totalWalkTime").asInt());
            summary.setTotalWalkDistance(first.path("totalWalkDistance").asInt());
            summary.setPathType(first.path("pathType").asInt());
            return summary;

        } catch (Exception e) {
            log.error("요약 경로 파싱 실패", e);
            throw new RuntimeException("요약 경로 파싱 실패", e);
        }
    }
}
