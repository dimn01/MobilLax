package MobilLax.Domain.Cart;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByEmail(String email);
    void deleteByEmail(String email); // 전체 삭제용

    // 🔥 그룹 단위 삭제 (UUID 기반 그룹 ID로 삭제)
    void deleteByEmailAndTotalFareGroupId(String email, String totalFareGroupId);

    // ✅ ID 기준으로 내림차순 정렬된 장바구니 항목 조회 (최근 항목이 위에 오도록)
    List<CartItemEntity> findByEmailOrderByIdDesc(String email);

}
