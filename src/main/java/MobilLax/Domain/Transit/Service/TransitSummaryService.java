package MobilLax.Domain.Transit.Service;

import MobilLax.Client.TransitApiClient;
import MobilLax.Domain.Transit.Dto.TransitRequestDto;
import MobilLax.Domain.Transit.Dto.TransitSummaryDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ 목적: Tmap API로부터 다중 경로 요약 정보를 파싱 및 가공하여 View에 전달
 */
@Service
@RequiredArgsConstructor
public class TransitSummaryService {

    private static final Logger log = LoggerFactory.getLogger(TransitSummaryService.class);

    private final ObjectMapper objectMapper;
    private final TransitApiClient transitApiClient;

    public List<TransitSummaryDto> getRouteSummaries(TransitRequestDto requestDto) {
        try {
            String response = transitApiClient.requestSummaryRoute(requestDto);
            log.info("🟢 [Tmap 요약 응답 수신]\n{}", response);
            return parseSummariesFromJson(response);
        } catch (Exception e) {
            log.error("❌ 다중 요약 경로 파싱 실패", e);
            throw new RuntimeException("Tmap 다중 요약 경로 파싱 오류", e);
        }
    }

    public List<TransitSummaryDto> getMockRouteSummaries() {
        try {
            Path path = Path.of("src/main/resources/mock/tmap_summary.json");
            String response = Files.readString(path, StandardCharsets.UTF_8);
            log.info("🟡 [Mock 데이터 요약 응답 수신]\n{}", response);
            return parseSummariesFromJson(response);
        } catch (Exception e) {
            log.error("❌ Mock 요약 경로 파일 읽기 실패", e);
            throw new RuntimeException("Mock 요약 JSON 파일 읽기 실패", e);
        }
    }

    private List<TransitSummaryDto> parseSummariesFromJson(String response) {
        try {
            JsonNode itineraryArray = objectMapper.readTree(response)
                    .path("metaData").path("plan").path("itineraries");

            List<TransitSummaryDto> list = new ArrayList<>();
            for (JsonNode node : itineraryArray) {
                list.add(buildSummaryFromNode(node));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("📁 다중 요약 응답 파싱 실패", e);
        }
    }

    private TransitSummaryDto buildSummaryFromNode(JsonNode node) {
        TransitSummaryDto dto = new TransitSummaryDto();
        dto.setTotalTime(node.path("totalTime").asInt());
        dto.setTotalFare(node.path("fare").path("regular").path("totalFare").asInt());
        dto.setTransferCount(node.path("transferCount").asInt());
        dto.setTotalDistance(node.path("totalDistance").asInt());
        dto.setTotalWalkDistance(node.path("totalWalkDistance").asInt());
        dto.setPathType(node.path("pathType").asInt());

        dto.formatAll(); // ⏱️ 사람이 읽기 쉬운 포맷으로 변환
        return dto;
    }
}
