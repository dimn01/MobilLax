package MobilLax.Controller;

import MobilLax.Service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;


    @Autowired
    private AuthenticationManager authenticationManager;

    // 로그인 페이지 요청 처리
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디나 비밀번호가 잘못되었습니다.");
        }
        return "member/login";  // 로그인 페이지 경로
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        boolean isAuthenticated = userAccountService.authenticateUser(email, password);

        if (isAuthenticated) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";  // 로그인 성공 시 홈으로 이동
        } else {
            model.addAttribute("error", "아이디나 비밀번호가 잘못되었습니다.");
            return "member/login";  // 로그인 실패 시 로그인 페이지로
        }
    }

    // 회원가입 페이지 요청 처리
    @GetMapping("/register")
    public String showSignupForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요.");
        }
        return "member/register";  // 회원가입 페이지 경로
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email, @RequestParam String password, Model model) {
        boolean success = userAccountService.registerUser(name, email, password);

        if (!success) {
            model.addAttribute("error", "이 이메일은 이미 사용 중입니다. 다른 이메일을 사용해주세요.");
            return "member/register";
        }

        return "redirect:/login";  // 회원가입 성공 시 로그인 페이지로
    }
}
