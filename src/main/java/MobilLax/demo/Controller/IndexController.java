package MobilLax.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  // 홈 페이지 요청 처리
  @GetMapping("/home.html")
  public String index() {
    return "home"; // templates/member/login.html
  }

  // 회원가입 페이지 요청 처리
  @GetMapping("/")
  public String showSignupForm() {
    return "home"; // templates/member/signup.html
  }
}
