package MobilLax.Domain.Payment.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String impUid;

    @Column(nullable = false)
    private String merchantUid;

    @Column(nullable = false)
    private String email;  // ✅ userId → email 로 통일

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String routeId;

    @Column(nullable = false)
    private String orderName; // ✅ 전체 결제명 (ex: "KTX, 고속버스")
}
