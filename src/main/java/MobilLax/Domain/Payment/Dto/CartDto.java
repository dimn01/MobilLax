package MobilLax.Domain.Payment.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * ✅ 사용자 장바구니 요청 DTO (프론트 → 백)
 * ✅ 사용자 식별 기준은 이메일 (userId → email)
 */
@Data
@AllArgsConstructor
public class CartDto {
    private String email;             // ✅ 사용자 이메일 (로그인 기준)
    private List<FareItem> items;    // ✅ 장바구니에 담긴 요금 항목

    public int getTotalAmount() {
        return items.stream().mapToInt(FareItem::getAmount).sum();
    }
}
