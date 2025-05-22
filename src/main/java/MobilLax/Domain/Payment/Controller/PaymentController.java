/*
 * PaymentController.java
 * ✅ 목적: PortOne 결제 완료 후 호출되는 /api/payment/confirm API를 처리하는 REST 컨트롤러
 * ✅ 기능: 결제 정보를 Payment 테이블에 저장
 */
package MobilLax.Domain.Payment.Controller;

import MobilLax.Domain.Payment.Dto.FareItem;
import MobilLax.Domain.Payment.Dto.PaymentDto;
import MobilLax.Domain.Payment.Model.Payment;
import MobilLax.Domain.Payment.Repository.PaymentRepository;
import MobilLax.Global.Response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "💰 결제 API", description = "PortOne 결제 완료 처리 및 저장 기능 제공")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    /**
     * ✅ 결제 완료 후 DB 저장 처리
     * @param dto 결제 요청 정보
     * @return 성공 메시지
     */
    @Operation(summary = "결제 정보 저장", description = "PortOne 결제 성공 후 결제 내역을 DB에 저장합니다.")
    @PostMapping("/confirm")
    public ApiResponse<String> confirmPayment(@RequestBody PaymentDto dto) {
        for (FareItem item : dto.getFareItems()) {
            Payment payment = Payment.builder()
                    .impUid(dto.getImpUid())
                    .merchantUid(dto.getMerchantUid())
                    .email(dto.getEmail())
                    .itemName(item.getName())
                    .amount(item.getAmount())
                    .routeId(item.getRouteId())
                    .orderName(dto.getOrderName()) // ✅ 통합된 필드
                    .build();

            paymentRepository.save(payment);
        }

        return ApiResponse.ok("✅ 결제가 성공적으로 저장되었습니다.");
    }
}
