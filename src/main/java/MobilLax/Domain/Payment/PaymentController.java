package MobilLax.Domain.Payment;

import MobilLax.Domain.Cart.CartItemEntity;
import MobilLax.Domain.Cart.CartItemRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;

    @Value("${portone_store}")
    private String storeId;

    @Value("${portone_channel_card}")
    private String channelKey;

    @PostMapping("/sdk-ready/{groupId}")
    public Map<String, Map<String, Object>> prepareSdkPayments(@PathVariable UUID groupId) {
        String email = getCurrentUserEmail();

        List<CartItemEntity> items = cartItemRepository.findByEmailAndTotalFareGroupId(email, groupId.toString());
        if (items.isEmpty()) throw new IllegalArgumentException("결제할 항목이 없습니다.");

        // ✅ 교통수단 종류별로 그룹화
        Map<String, List<CartItemEntity>> groupedByTransport = items.stream()
                .collect(Collectors.groupingBy(CartItemEntity::getMode));

        Map<String, Map<String, Object>> result = new HashMap<>();

        for (Map.Entry<String, List<CartItemEntity>> entry : groupedByTransport.entrySet()) {
            String transport = entry.getKey();
            List<CartItemEntity> transportItems = entry.getValue();

            int totalAmount = transportItems.stream().mapToInt(CartItemEntity::getRoutePayment).sum();

            String orderName = transportItems.size() == 1 ?
                    transportItems.get(0).getStartName() + " → " + transportItems.get(0).getEndName() :
                    transportItems.get(0).getStartName() + " → " + transportItems.get(transportItems.size() - 1).getEndName()
                            + " 외 " + (transportItems.size() - 1) + "건";

            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("storeId", storeId);
            paymentInfo.put("channelKey", channelKey);
            paymentInfo.put("paymentId", "payment-" + UUID.randomUUID());
            paymentInfo.put("orderName", orderName);
            paymentInfo.put("amount", totalAmount);
            paymentInfo.put("transportType", transport);

            result.put(transport, paymentInfo);
        }

        return result;
    }


    // ✅ [2] 결제 완료 정보 저장
    @PostMapping("/complete")
    public String completePayment(@RequestBody PaymentCompleteRequest req) {
        String email = getCurrentUserEmail();

        // 중복 결제 방지
        boolean alreadyPaid = paymentRepository.existsByGroupIdAndStatus(req.getGroupId(), PaymentEntity.PaymentStatus.SUCCESS);
        if (alreadyPaid) {
            return "이미 결제된 주문입니다.";
        }

        PaymentEntity entity = PaymentEntity.builder()
                .email(email)
                .amount(req.getAmount())
                .date(LocalDate.now())
                .groupId(req.getGroupId())
                .transportType(req.getTransportType()) // 🔥 교통수단 저장
                .status(PaymentEntity.PaymentStatus.SUCCESS)
                .build();

        paymentRepository.save(entity);
        cartItemRepository.deleteByEmailAndTotalFareGroupId(email, req.getGroupId());

        return "ok";
    }



    // ✅ [3] 결제 실패 정보 저장
    @PostMapping("/fail")
    public String failPayment(@RequestBody PaymentCompleteRequest req) {
        String email = getCurrentUserEmail();

        boolean alreadySuccess = paymentRepository.existsByGroupIdAndStatus(req.getGroupId(), PaymentEntity.PaymentStatus.SUCCESS);
        if (alreadySuccess) {
            return "이미 성공한 주문입니다.";
        }

        boolean alreadyFailed = paymentRepository.existsByGroupIdAndStatus(req.getGroupId(), PaymentEntity.PaymentStatus.FAIL);
        if (alreadyFailed) {
            return "이미 실패로 기록된 주문입니다.";
        }

        PaymentEntity entity = PaymentEntity.builder()
                .email(email)
                .amount(req.getAmount())
                .date(LocalDate.now())
                .groupId(req.getGroupId())
                .transportType(req.getTransportType())  // 🔥 교통수단 저장
                .status(PaymentEntity.PaymentStatus.FAIL)
                .build();

        paymentRepository.save(entity);
        return "fail_saved";
    }


    // ✅ 인증된 사용자 이메일 조회 유틸
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @Getter
    @Setter
    public static class PaymentCompleteRequest {
        private String groupId;
        private String paymentId;
        private int amount;
        private String transportType;  // 🔥 추가됨
    }
}
