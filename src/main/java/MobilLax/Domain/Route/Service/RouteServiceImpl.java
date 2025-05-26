package MobilLax.Domain.Route.Service;

import MobilLax.Domain.Route.Repository.RouteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import MobilLax.Domain.Route.Dto.RouteResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {

    @Override
    public RouteResponse getRoute(String type) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://apis.openapi.sk.com/transit/routes";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("appKey", "🔑여기에_TMAP_실제_키_입력");

            Map<String, Object> body = Map.of(
                    "startX", 127.0,
                    "startY", 37.0,
                    "endX", 127.1,
                    "endY", 37.1,
                    "count", 10,
                    "lang", 0,
                    "format", "json"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // ✅ JSON → RouteResponse 직접 매핑
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), RouteResponse.class);

        } catch (Exception e) {
            System.out.println("❌ Tmap API 실패, 더미 데이터로 fallback");

            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(
                        new ClassPathResource("static/javascript/dummy/route_dummy_data.json").getInputStream(),
                        RouteResponse.class
                );
            } catch (IOException ioException) {
                throw new RuntimeException("더미 JSON 로딩 실패", ioException);
            }
        }
    }
}