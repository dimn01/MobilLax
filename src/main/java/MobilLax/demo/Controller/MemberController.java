package MobilLax.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    // 로그인 페이지 요청 처리
    @GetMapping("/login")
    public String showLoginForm() {
        return "member/login"; // templates/member/login.html
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/signup")
    public String showSignupForm() {
        return "member/signup"; // templates/member/signup.html
    }

    // 로그아웃 (요청 방식에 따라 수정 가능)
    @GetMapping("/logout")
    public String logout() {
        // 세션 무효화 등 로그아웃 로직을 구현할 수 있습니다.
        return "redirect:/login"; // 로그아웃 후 로그인 페이지로 이동
    }
}
