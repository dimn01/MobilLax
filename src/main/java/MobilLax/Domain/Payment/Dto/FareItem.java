package MobilLax.Domain.Payment.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ 구간 요금 항목 (장바구니/결제에 공통 사용)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareItem {
    private String id;         // UI에서 사용되는 고유 키 (선택)
    private String name;       // 구간 이름
    private int amount;        // 구간 금액
    private String routeId;    // 노선 ID
}
