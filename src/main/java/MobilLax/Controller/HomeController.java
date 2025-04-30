package MobilLax.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping({"/", "/home"})
  public String home(Model model) {
    return "home";  // templates/home.html 로 바로 이동
  }
}