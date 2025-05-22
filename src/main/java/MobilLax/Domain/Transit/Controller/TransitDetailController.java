package MobilLax.Domain.Transit.Controller;

import MobilLax.Domain.User.Model.CustomUserDetails;
import MobilLax.Domain.User.Model.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TransitDetailController.java
 *
 * ✅ 목적: 상세 경로 화면 요청 시 사용자 정보를 모델에 담아 Thymeleaf에 전달
 * ✅ 경로: /transit/detail (HTML 화면 렌더링)
 */
@Controller
public class TransitDetailController {

    /**
     * ✅ 상세 경로 페이지 요청 핸들러
     * 로그인 여부에 따라 사용자 정보를 모델에 담아 화면에 전달합니다.
     *
     * @param model         Thymeleaf 렌더링에 사용될 모델
     * @param userDetails   로그인한 사용자의 CustomUserDetails (없을 수도 있음)
     * @return              transit/detail.html 또는 detail_route.html 템플릿 렌더링
     */
    @GetMapping("/transit/detail")
    public String showTransitDetailPage(Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            UserAccount user = userDetails.getUser();
            model.addAttribute("customerId", user.getId().toString()); // 또는 user.getEmail()
            model.addAttribute("customerName", user.getName());
            model.addAttribute("customerEmail", user.getEmail());
        } else {
            // ⚠️ 비로그인 사용자 기본값 처리
            model.addAttribute("customerId", "guest");
            model.addAttribute("customerName", "이름없음");
            model.addAttribute("customerEmail", "noemail@example.com");
        }

        // ✅ 템플릿 경로 명확히 확인 필요 (예: detail_route.html)
        return "detail_route";
    }
}
