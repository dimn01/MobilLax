package MobilLax.Domain.Route.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/route")
    public String routePage() {
        return "route"; // → templates/route.html 로 렌더링됨
    }
}