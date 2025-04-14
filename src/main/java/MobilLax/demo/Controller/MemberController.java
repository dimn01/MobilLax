package MobilLax.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
public class MemberController {

    // 로그인 페이지 반환
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        // 실제 로그인 처리 로직 (예: DB 연동 등은 서비스에서 구현)
        // 현재는 간단한 예시
        if (username.equals("admin") && password.equals("1234")) {
            return "redirect:/"; // 로그인 성공 시 홈으로 리다이렉트
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "login"; // 로그인 페이지로 다시 이동
        }
    }

    // 회원가입 페이지 반환
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup"; // templates/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         Model model) {
        // 실제 회원가입 로직 (예: 서비스 통해 DB에 저장 등)
        // 현재는 간단한 성공 처리
        return "redirect:/member/login";
    }

    // 로그아웃 처리
    @GetMapping("/signout")
    public String signout() {
        // 세션 또는 인증 정보 제거 등 처리
        return "redirect:/";
    }
}