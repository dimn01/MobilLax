/*
 * UserMypageController.java
 * ✅ 목적: 로그인한 사용자의 마이페이지 기능 제공 (내 정보, 장바구니)
 * ✅ 경로: /mypage/info, /mypage/cart
 */

package MobilLax.Domain.User.Controller;

import MobilLax.Domain.Payment.Model.Payment;
import MobilLax.Domain.Payment.Dto.CartDto;
import MobilLax.Domain.Payment.Dto.FareItem;
import MobilLax.Domain.Payment.Repository.PaymentRepository;
import MobilLax.Domain.User.Model.UserAccount;
import MobilLax.Domain.User.Repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserMypageController {

    private final UserAccountRepository userAccountRepository;
    private final PaymentRepository paymentRepository;

    /**
     * ✅ 로그인한 사용자의 정보 페이지
     */
    @GetMapping("/info")
    public String myInfo(Model model) {
        String email = getCurrentUserEmail();

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "mypage/info";
    }

    /**
     * ✅ 로그인한 사용자의 결제된 장바구니 내역 보기
     */
    @GetMapping("/cart")
    public String myCart(Model model) {
        String email = getCurrentUserEmail();

        List<Payment> payments = paymentRepository.findByEmail(email);
        List<FareItem> fareItems = payments.stream()
                .map(p -> new FareItem(
                        p.getId().toString(),
                        p.getItemName(),
                        p.getAmount(),
                        p.getRouteId()))
                .toList();

        model.addAttribute("cart", new CartDto(email, fareItems));
        model.addAttribute("email", email);
        return "mypage/cart";
    }

    /**
     * ✅ 현재 로그인한 사용자 이메일 반환
     */
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); // 이메일을 principal로 사용하는 구조
    }
}
