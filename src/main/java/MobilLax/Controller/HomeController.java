package MobilLax.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping({"/", "/home"})
  public String home() {
    return "home";  // home.html이 templates 폴더에 있어야 합니다.
  }
}
