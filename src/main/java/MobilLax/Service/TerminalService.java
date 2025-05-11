package MobilLax.Service;

import MobilLax.Config.BusApiProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TerminalService {

    private final WebClient webClient;
    private final BusApiProperties busApiProperties;

    public TerminalService(WebClient webClient, BusApiProperties busApiProperties) {
        this.webClient = webClient;
        this.busApiProperties = busApiProperties;
    }

    public Mono<String> getTerminalList(String terminalName) {
        String encodedTerminalName = URLEncoder.encode(terminalName, StandardCharsets.UTF_8);
        String serviceKey = busApiProperties.getKey();  // 반드시 URL 인코딩된 키 사용

        URI uri = URI.create(String.format(
                "https://apis.data.go.kr/1613000/SuburbsBusInfoService/getSuberbsBusTrminlList" +
                        "?serviceKey=%s&_type=xml&terminalNm=%s",
                serviceKey, encodedTerminalName
        ));

        System.out.println("✅ 최종 URI: " + uri);

        return webClient.get()
                .uri(uri)
                .header("Accept", "application/xml")
                .retrieve()
                .bodyToMono(String.class);
    }
}