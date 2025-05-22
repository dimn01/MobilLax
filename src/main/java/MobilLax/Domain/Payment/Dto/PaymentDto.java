package MobilLax.Domain.Payment.Dto;

import lombok.Data;
import java.util.List;

/**
 * ✅ 결제 완료 후 서버로 전달되는 DTO (PortOne → 서버)
 */
@Data
public class PaymentDto {
    private String email;
    private String merchantUid;
    private String impUid;
    private String orderName;
    private int totalAmount;
    private List<FareItem> fareItems;
}
