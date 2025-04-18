package MobilLax.Controller;

import MobilLax.Service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MemberController {

    @Autowired
    private UserAccountService userAccountService;

    // 로그인 페이지 요청 처리
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // 로그인 페이지
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/signup")
    public String showSignupForm() {
        return "register";  // 회원가입 페이지
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ModelAndView signup(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        boolean success = userAccountService.registerUser(name, email, password);

        ModelAndView mv = new ModelAndView();
        if (success) {
            mv.setViewName("redirect:/login");  // 회원가입 후 로그인 페이지로 리다이렉트
        } else {
            mv.setViewName("redirect:/signup?error=email_exists");  // 이메일 중복 시 다시 회원가입 페이지로
        }

        return mv;
    }
}
