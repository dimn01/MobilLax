/*
 * PortOneService.java
 * ✅ 목적: PortOne v2 API를 통해 결제 내역 검증 수행 (secret 단독 사용)
 */
package MobilLax.Domain.Payment.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PortOneService {

    private final WebClient webClient;

    @Value("${portone.client.secret}")
    private String clientSecret;

    /**
     * ✅ 결제 고유 ID(imp_uid 또는 payment_id)를 이용해 결제 금액 확인 (v2 방식)
     */
    public int getPaymentAmount(String paymentId) {
        JsonNode response = webClient.get()
                .uri("https://api.portone.io/v2/payments/" + paymentId)
                .header("Authorization", "Bearer " + clientSecret)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return response.path("data").path("amount").asInt();
    }
}