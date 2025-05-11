package MobilLax.Controller;

import MobilLax.Service.TerminalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TerminalController {

    private final TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping(value = "/api/terminals", produces = "application/xml")
    public Mono<String> getTerminalList(@RequestParam String name) {
        return terminalService.getTerminalList(name);
    }
}