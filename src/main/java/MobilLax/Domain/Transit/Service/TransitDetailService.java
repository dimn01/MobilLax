package MobilLax.Domain.Transit.Service;

import MobilLax.Client.TransitApiClient;
import MobilLax.Domain.Transit.Dto.TransitDetailDto;
import MobilLax.Domain.Transit.Dto.TransitRequestDto;
import MobilLax.Global.Util.FormatUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransitDetailService {

    private static final Logger log = LoggerFactory.getLogger(TransitDetailService.class);

    private final ObjectMapper objectMapper;
    private final TransitApiClient transitApiClient;

    public List<TransitDetailDto> getRouteDetails(TransitRequestDto requestDto) {
        try {
            String response = transitApiClient.requestDetailRoute(requestDto);
            log.info("🟡 [Tmap 상세 응답 수신]\n{}", response);
            return parseDetailsFromJson(response);
        } catch (Exception e) {
            log.error("❌ Tmap 상세 경로 파싱 실패", e);
            throw new RuntimeException("Tmap 상세 경로 파싱 오류", e);
        }
    }

    public List<TransitDetailDto> getMockRouteDetails() {
        try {
            Path path = Path.of("src/main/resources/mock/tmap_detail.json");
            String response = Files.readString(path, StandardCharsets.UTF_8);
            log.info("🟡 [Mock 데이터 상세 응답 수신]\n{}", response);
            return parseDetailsFromJson(response);
        } catch (Exception e) {
            log.error("❌ Tmap 상세 경로 파싱 실패", e);
            throw new RuntimeException("Tmap 상세 경로 파싱 오류", e);
        }
    }

    public List<TransitDetailDto> parseDetailsFromJson(String response) {
        try {
            JsonNode itineraries = objectMapper.readTree(response)
                    .path("metaData").path("plan").path("itineraries");

            if (!itineraries.isArray() || itineraries.isEmpty())
                throw new RuntimeException("상세 경로가 존재하지 않음");

            List<TransitDetailDto> resultList = new ArrayList<>();
            for (JsonNode itinerary : itineraries) {
                resultList.add(buildDetailFromNode(itinerary));
            }
            return resultList;
        } catch (Exception e) {
            throw new RuntimeException("📁 다중 상세 응답 파싱 실패", e);
        }
    }

    private TransitDetailDto buildDetailFromNode(JsonNode itinerary) {
        TransitDetailDto dto = new TransitDetailDto();
        dto.setTotalTime(itinerary.path("totalTime").asInt());
        dto.setTotalFare(itinerary.path("fare").path("regular").path("totalFare").asInt());
        dto.setTransferCount(itinerary.path("transferCount").asInt());

        List<TransitDetailDto.TransitLeg> legs = new ArrayList<>();

        for (JsonNode jsonLeg : itinerary.path("legs")) {
            TransitDetailDto.TransitLeg leg = objectMapper.convertValue(jsonLeg, TransitDetailDto.TransitLeg.class);
            leg.setPassShapeLineString(jsonLeg.path("passShape").path("linestring").asText(null));

            if (leg.getStations() == null) leg.setStations(Collections.emptyList());
            if (leg.getLanes() == null && jsonLeg.has("Lane")) {
                List<TransitDetailDto.Lane> lanes = new ArrayList<>();
                for (JsonNode laneNode : jsonLeg.get("Lane")) {
                    lanes.add(objectMapper.convertValue(laneNode, TransitDetailDto.Lane.class));
                }
                leg.setLanes(lanes);
            } else if (leg.getLanes() == null) {
                leg.setLanes(Collections.emptyList());
            }

            leg.setFormattedTime(FormatUtil.formatTime(leg.getSectionTime()));
            leg.setFormattedDistance(FormatUtil.formatDistance(leg.getDistance()));
            legs.add(leg);
        }

        for (int i = 0; i < legs.size(); i++) {
            TransitDetailDto.TransitLeg leg = legs.get(i);
            String mode = leg.getMode();

            if (!"WALK".equals(mode)) {
                leg.setSteps(Collections.emptyList());
                continue;
            }

            TransitDetailDto.TransitLeg prev = (i > 0) ? legs.get(i - 1) : null;
            TransitDetailDto.TransitLeg next = (i < legs.size() - 1) ? legs.get(i + 1) : null;

            String startName = leg.getStart() != null ? leg.getStart().getName() : "출발지";
            String endName = leg.getEnd() != null ? leg.getEnd().getName() : "도착지";

            boolean involvesTrain = (prev != null && "TRAIN".equals(prev.getMode())) ||
                    (next != null && "TRAIN".equals(next.getMode()));
            boolean involvesBus = (prev != null && isBusMode(prev.getMode())) ||
                    (next != null && isBusMode(next.getMode()));

            TransitDetailDto.Step step = new TransitDetailDto.Step();

            if (involvesTrain && involvesBus && leg.getDistance() > 0) {
                if (isLikelyStation(endName)) leg.getEnd().setName(applySuffix(endName, "TRAIN"));
                if (isLikelyStation(startName)) leg.getStart().setName(applySuffix(startName, "TRAIN"));
                step.setDescription(startName + "에서 " + endName + "까지 환승 이동");
                leg.setSteps(List.of(step));
            } else if (leg.getDistance() <= 100 && leg.getDistance() > 0) {
                step.setDescription(startName + "에서 " + endName + "까지 약 " + FormatUtil.formatDistance(leg.getDistance()) + " 도보 이동");
                leg.setSteps(List.of(step));
            } else {
                leg.setSteps(Collections.emptyList());
            }
        }

        for (int i = 0; i < legs.size(); i++) {
            TransitDetailDto.TransitLeg leg = legs.get(i);
            if ("WALK".equals(leg.getMode())) {
                if (i == 0) {
                    leg.setRole("출발지 → 출발지 주변역까지 도보 이동");
                } else if (i == legs.size() - 1) {
                    leg.setRole("도착역 → 도착지까지 도보 이동");
                } else {
                    leg.setRole("환승을 위한 도보 이동");
                }
            } else {
                String transport = switch (leg.getMode()) {
                    case "TRAIN" -> "기차 탑승";
                    case "EXPRESSBUS" -> "고속버스 탑승";
                    case "SUBWAY" -> "지하철 탑승";
                    case "BUS" -> "버스 탑승";
                    default -> "이동";
                };
                leg.setRole(leg.getStart().getName() + " → " + leg.getEnd().getName() + ": " + transport);
            }
        }

        dto.setLegs(legs);
        return dto;
    }

    private boolean isBusMode(String mode) {
        return mode != null && Set.of("BUS", "EXPRESSBUS", "INTERCITYBUS").contains(mode);
    }

    private boolean isLikelyStation(String name) {
        if (name == null) return false;
        return name.contains("역") || name.endsWith("역") || name.endsWith("터미널");
    }

    private String applySuffix(String name, String mode) {
        if (name == null || name.isBlank()) return "";
        return switch (mode) {
            case "TRAIN" -> name.endsWith("역") ? name : name + "역";
            case "EXPRESSBUS", "INTERCITYBUS" -> name.endsWith("터미널") ? name : name + "터미널";
            default -> name;
        };
    }
}
