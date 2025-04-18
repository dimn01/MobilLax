package MobilLax.Controller;

import MobilLax.Model.UserAccount;
import MobilLax.Service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class MemberController {

    @Autowired
    private UserAccountService userAccountService;

    // 홈 페이지 요청 처리
    @GetMapping("/home")
    public String home() {
        return "home";  // 홈 페이지
    }

    // 로그인 페이지 요청 처리
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // 로그인 페이지
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("userAccount", new UserAccount());  // 새로운 UserAccount 객체 추가
        return "signup";  // 회원가입 페이지
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@Valid UserAccount userAccount, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";  // 유효성 검사 오류 시 다시 회원가입 페이지로
        }

        // 이메일 중복 확인
        if (userAccountService.existsById(userAccount.getId())) {
            result.rejectValue("id", "error.user", "이미 등록된 이메일입니다.");
            return "signup";  // 이메일 중복 시 다시 회원가입 페이지로
        }

        // 회원가입 처리
        userAccountService.register(userAccount);  // 비밀번호 암호화 및 저장
        return "redirect:/login";  // 회원가입 후 로그인 페이지로 리다이렉트
    }
}
