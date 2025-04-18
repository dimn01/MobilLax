package MobilLax.Controller;
import MobilLax.Model.User;
import MobilLax.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ModelAndView register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password
    ) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // 실제 서비스에선 비밀번호 암호화 필요

        boolean success = userService.registerUser(user);
        ModelAndView mv = new ModelAndView();

        if (success) {
            mv.setViewName("redirect:/login.html");
        } else {
            mv.setViewName("redirect:/register.html?error=email_exists");
        }
        return mv;
    }
}
