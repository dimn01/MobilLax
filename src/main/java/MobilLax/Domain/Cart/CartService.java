package MobilLax.Domain.Cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;

    public int saveCartItems(List<CartItemRequestDTO.LegDTO> legs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String groupId = UUID.randomUUID().toString();
        int totalFare = 0;

        for (CartItemRequestDTO.LegDTO leg : legs) {
            // ✅ 도보이거나 요금이 null/0이면 저장하지 않음
            if (leg.getRoutePayment() <= 0) {
                System.out.println("❌ 저장 제외: " + leg.getMode() + " - " + leg.getStartName() + " → " + leg.getEndName());
                continue;
            }

            CartItemEntity item = CartItemEntity.builder()
                    .email(email)
                    .mode(leg.getMode())
                    .route(leg.getRoute())
                    .routeId(leg.getRouteId())
                    .routePayment(leg.getRoutePayment())
                    .startName(leg.getStartName())
                    .endName(leg.getEndName())
                    .totalFareGroupId(groupId)
                    .build();

            cartItemRepository.save(item);
            totalFare += leg.getRoutePayment();
        }

        return totalFare;
    }


    public Map<String, List<CartItemEntity>> getGroupedCartItems() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartItemEntity> items = cartItemRepository.findByEmail(email);
        return items.stream()
                .collect(Collectors.groupingBy(CartItemEntity::getTotalFareGroupId));
    }

    public void clearCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        cartItemRepository.deleteByEmail(email);
    }

    public void deleteGroup(String groupId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        cartItemRepository.deleteByEmailAndTotalFareGroupId(email, groupId);
    }
}
