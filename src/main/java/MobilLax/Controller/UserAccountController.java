package MobilLax.Controller;

import MobilLax.Service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    // 로그인 페이지
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 잘못되었습니다.");
        }
        return "member/login";
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String showSignupForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "member/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email, @RequestParam String password, Model model, RedirectAttributes redirectAttributes) {
        try {
            userAccountService.registerUser(name, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입 되었습니다.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member/register";
        }
    }
}
